package asp.android.asppagos.ui.fragments.codi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.navigation.NavOptions
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.databinding.FragmentCodiDevDetailBinding
import asp.android.asppagos.ui.adapters.MovementType
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.mapToUri
import asp.android.asppagos.ui.fragments.mapToUriMedia
import asp.android.asppagos.utils.formatAsMoney
import com.google.gson.Gson
import com.nmd.screenshot.Screenshot
import java.io.IOException


class CodiDevDetailFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    override var TAG: String = this.javaClass.name
    private var _binding: FragmentCodiDevDetailBinding? = null
    private val binding get() = _binding!!
    private var isPayment = false

    /**
     * Read External Storage permission
     */
    private var isReadPermissionGranted = false

    /**
     * Write External Storage permission
     */
    private var isWritePermissionGranted = false

    /**
     * Read Media Images permission (Android 33)
     */
    private var isMediaImagesPermissionGranted = false

    /**
     * Launcher to handle permissions request result
     */
    private val multipleRequestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // Update permissions status
        // For Android 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isMediaImagesPermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: isMediaImagesPermissionGranted
        } else {
            isReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        }

        // Check if all permissions are granted and launch sharing intent
        if (checkForPermissions()) {
            shareTransaction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCodiDevDetailBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        binding.let {
            parentFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->

                val data = Gson().fromJson(bundle.getString("data")!!, CodiMovResponse::class.java)
                data.let { d ->
                    isPayment = true
                    it.crValue.text = d.cr
                    it.ownerName.text = d.owner
                    it.refValue.text = d.reference
                    it.amountDetail.text = d.amount.toString().formatAsMoney(MovementType.TRANSFER)
                    when (d.incomeFrom) {
                        "qrPayment" -> {
                            it.txtMain.text = "El pago fue realizado"
                            this.binding.accountDetailToolbar.setTitle("Pagar con CoDi")
                        }

                        "dev" -> {
                            this.binding.accountDetailToolbar.setTitle("Devolucion")
                        }

                        "qrRejected" -> {
                            it.txtMain.text = "El pago fue rechazado"
                            it.successDevPay.setBackgroundResource(asp.android.aspandroidmaterial.R.drawable.ic_error)
                            this.binding.accountDetailToolbar.setTitle("Pagar con CoDi")
                            this.binding.crDetail.text = ""
                            this.binding.ivShareQR.visibility = View.GONE
                            this.binding.txtShare.visibility = View.GONE
                        }
                    }
                }
            }

            it.ivShareQR.setOnClickListener {
                /*
                if (!checkForPermissions()) {
                    requestMultiplePermissions()
                } else {
                    shareTransaction()
                }*/
                shareScreenshot()
            }

            it.confirmarBtnDetailPagar.setOnClickListener {
                if (!isPayment) {
                    safeNavigate(R.id.action_codiDevDetailFragment_to_codiMovFragment)
                } else {
                    safeNavigate(R.id.action_codiDevDetailFragment_to_coDiModuleFragment)
                }
            }
        }
    }

    private fun shareTransaction() {
        try {
            val bmpUri = binding.accountDetailToolbarMain.drawToBitmap(Bitmap.Config.ARGB_8888)
                .mapToUriMedia(requireContext())

            if (bmpUri != null) {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
                shareIntent.type = "image/*"
                startActivity(Intent.createChooser(shareIntent, "Compartir QR"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareScreenshot() {
        (requireActivity() as AppCompatActivity).apply {
            Screenshot(this).apply {
                fileName = "ASP_screenshot.jpg"
                saveScreenshot = true
                takeScreenshot(object : Screenshot.OnResultListener {

                    override fun result(success: Boolean, bitmap: Bitmap?) {
                        if (success) {
                            bitmap?.let {
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    bitmap.mapToUri(requireContext())
                                )
                                shareIntent.type = "image/*"
                                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                requireContext().startActivity(shareIntent)
                            }
                        } else {
                            Log.e(TAG, "ERROR AL COMPARTIR LA IMAGEN")
                        }
                    }
                })
            }
        }
    }

    /*
    private fun getLocalBitmapUri(imageView: ImageView): Uri? {

        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp: Bitmap? = null
        bmp = if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
        var bmpUri: Uri? = null
        try {


            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = bmp?.mapToUriMedia(requireContext())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    private fun requestPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE, false
                ) -> {

                }

                permissions.getOrDefault(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE, false
                ) -> {

                }

                else -> {

                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    */


    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
        this.binding.accountDetailToolbar.isVisibleBackButton(false)
    }

    override fun onClickBackButton() {
        childFragmentManager.executePendingTransactions()
        safeNavigate(
            R.id.action_codiDevDetailFragment_to_coDiModuleFragment,
            args = null,
            navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.action_codiDevDetailFragment_to_coDiModuleFragment, inclusive = true)
            }.build()
        )
    }

    /**
     * Check if all needed permissions have been granted.
     * @return True if all permissions are granted, false otherwise.
     */
    private fun checkForPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isMediaImagesPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            isMediaImagesPermissionGranted
        }
        else {
            isWritePermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            isReadPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            isReadPermissionGranted && isWritePermissionGranted
        }
    }

    /**
     * Method to request needed permissions, according to OS version.
     */
    private fun requestMultiplePermissions() {
        val permissionsList : MutableList<String> = ArrayList()

        // For Android 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isMediaImagesPermissionGranted) {
                permissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            if (permissionsList.isNotEmpty()) {
                multipleRequestPermissionLauncher.launch(permissionsList.toTypedArray())
            }

        }
        else {
            if (!isWritePermissionGranted) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }

            if (!isReadPermissionGranted) {
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (permissionsList.isNotEmpty()) {
                multipleRequestPermissionLauncher.launch(permissionsList.toTypedArray())
            }

        }
    }

}