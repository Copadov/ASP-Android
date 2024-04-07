package asp.android.asppagos.ui.fragments.onboarding


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialLoadingScreenOCR
import asp.android.asppagos.R
import asp.android.asppagos.data.models.OCRRequestData
import asp.android.asppagos.databinding.FragmentOcrDataValidationBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showSingleButtonDialog
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class OcrDataValidationFragment : BaseFragment() {

    private lateinit var file: File
    private var _binding: FragmentOcrDataValidationBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    private val requestImageFromFront = 1
    private val requestImageFromRear = 2
    private val permissionCode = 1000

    /**
     * Variable to validate if OCR dialog is already showing
     */
    private var isShowingDialog = false

    override var TAG: String = this.javaClass.name

    companion object {
        /**
         * Image max width resolution
         */
        const val MAX_WIDTH = 1280

        /**
         * Image max height resolution
         */
        const val MAX_HEIGHT = 720
    }

    private fun initViewModel() {

        viewModel.let {
            it.ocrDataResponse.observe(viewLifecycleOwner) { ocrResponse ->
                dialogOCR.dismiss()
                isShowingDialog = false
                if (ocrResponse.curp.isEmpty()) {
                    showCustomDialogError(
                        getString(R.string.information_dialog_text),
                        getString(R.string.information_ocr_text_dialog)
                    )
                } else {
                    safeNavigate(R.id.action_ocrDataValidation_to_dataConfirmationFragment)
                }
            }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialogOCR.dismiss()
                isShowingDialog = false
                showCustomDialogError(getString(R.string.information_dialog_text), errorMessage)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOcrDataValidationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    openCamera(requestCode)
                } else {
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogOCR = ASPMaterialLoadingScreenOCR()

        initViewModel()

        binding.let {

            it.ASPTMaterialToolbarOCRDataValidation.setASPMaterialToolbarsListeners(this)

            it.captureFrontButton.setOnClickListener {
                validateCamera(requestImageFromFront)
            }

            it.captureRearButton.setOnClickListener {
                validateCamera(requestImageFromRear)
            }

            it.continueButton.setOnClickListener {
                if (!isShowingDialog) {
                    dialogOCR.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )
                    isShowingDialog = true
                }
                viewModel.getOCRData(OCRRequestData(viewModel.front, viewModel.rear))
            }

            it.ivHelpFront.setOnClickListener {
                val infoFront = FragmentIneHelp.newInstance(
                    R.drawable.ine_front_bottom
                )
                infoFront.show(childFragmentManager, TAG)
            }

            it.ivHelpRear.setOnClickListener {
                val infoRear = FragmentIneHelp.newInstance(
                    R.drawable.ine_rear_bottom
                )
                infoRear.show(childFragmentManager, TAG)
            }

            initViewModel()
        }
    }

    private fun validateCamera(requestCode: Int) {
        openCamera(requestCode)
    }

    @SuppressLint("SimpleDateFormat")
    private fun openCamera(requestCode: Int) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, getString(R.string.asp_picture))
        values.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.from_the_camera_text))

        // Camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Set filename
        val timeStamp =
            SimpleDateFormat(getString(R.string.date_format_text)).format(Date())
        val storageDir: File =
            requireActivity()
                .applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        file = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )

        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            requireActivity().applicationContext.packageName + ".provider",
            file
        )

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        val manager =
            requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = manager.cameraIdList
        var rearCameraId: String? = null
        for (cameraId in cameraIdList) {
            val characteristics = manager.getCameraCharacteristics(cameraId)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                rearCameraId = cameraId
                break
            }
        }

        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", rearCameraId)

        startActivityForResult(cameraIntent, requestCode)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {

            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().applicationContext.packageName + ".provider",
                file
            );

            val source = ImageDecoder.createSource(file)

            val bitmap = ImageDecoder.decodeBitmap(source)

            val scaledImage = scaleImage(bitmap)

            when (requestCode) {
                requestImageFromFront -> {
                    binding.imageViewFront.setImageURI(uri)
                    viewModel.front = encodeToBase64(scaledImage, Bitmap.CompressFormat.PNG, 80)!!
                }

                requestImageFromRear -> {
                    binding.imageViewRear.setImageURI(uri)
                    viewModel.rear = encodeToBase64(scaledImage, Bitmap.CompressFormat.PNG, 80)!!
                }
            }

            validateButtonVisibility()
        } else {

        }
    }

    private fun scaleImage(bm: Bitmap): Bitmap {
        var width: Int = bm.width
        var height: Int = bm.height

        if (width > height) {
            // landscape
            val ratio = width / MAX_WIDTH
            width = MAX_WIDTH
            height /= ratio
        } else if (height > width) {
            // portrait
            val ratio = height / MAX_HEIGHT
            height = MAX_HEIGHT
            width /= ratio
        } else {
            // square
            height = MAX_HEIGHT
            width = MAX_WIDTH
        }

        return Bitmap.createScaledBitmap(bm, width, height, true)
    }

    private fun validateButtonVisibility() {
        if (viewModel.front.isNotEmpty() && viewModel.rear.isNotEmpty()) {
            binding.continueButton.visibility = View.VISIBLE
        }
    }

    override fun onClickBackPressed() {
        findNavController().popBackStack()
    }

    override fun onClickWhatsappIcon() {
        requireActivity().showSingleButtonDialog(
            getString(R.string.information_dialog_text),
            getString(R.string.call_center_text),
            getString(R.string.accept)
        )
    }

    private fun encodeToBase64(
        image: Bitmap,
        compressFormat: Bitmap.CompressFormat?,
        quality: Int
    ): String? {
        val byteArrayOS = ByteArrayOutputStream()
        image.compress(compressFormat!!, quality, byteArrayOS)
        return Base64.getEncoder().encodeToString(byteArrayOS.toByteArray())
    }
}