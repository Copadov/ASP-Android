package asp.android.asppagos.ui.fragments.codi


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.nfc.*
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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.BuildConfig
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CobroReq
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.databinding.FragmentCobrarQrCodiBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.fragments.mapToUri
import asp.android.asppagos.ui.fragments.mapToUriMedia
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.nmd.screenshot.Screenshot
import java.io.IOException
import java.text.DecimalFormat


class CobrarQrCodi :  BaseFragment(), ASPMaterialToolbarMainDashboard
.ASPMaterialToolbarMainDashboardListeners/*, NfcAdapter.CreateNdefMessageCallback*/  {

    override var TAG: String = this.javaClass.name

    private var _binding: FragmentCobrarQrCodiBinding? = null
    private val binding get() = _binding!!
    private  var _data : CobroRequest? = null;

    //var nfcAdapter: NfcAdapter? = null
    //lateinit var nfcMessage: String

    private val  data get() = _data!!;
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCobrarQrCodiBinding.inflate(inflater, container, false)
        findNavController().enableOnBackPressed(true);
        return binding.root;
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        //nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())

        binding.let {
            requestPermissions(it)
            it.ivShareQR.setOnClickListener {
                compartirQR()
                //shareScreenshot()
            }
            parentFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
                val result = bundle.getString("data")
                Log.d(TAG,"${result}")
                val galleryValue:CobroRequest = Gson().fromJson(result!!,CobroRequest::class.java)
                _data=galleryValue
                Log.d(TAG,"${galleryValue.toJson()}")

                it.amountNumber.text ="Sin monto"
                galleryValue.AMO.let {v->
                   if(v !=null){
                       val formater = DecimalFormat("0.00")
                       it.amountNumber.text ="$${formater.format(v)}"
                   }
                }
                it.userName.text=galleryValue.v?.NAM
                it.accountNumber.text=galleryValue.v?.ACC
                it.conceptTxt.text = galleryValue.DES!!

                it.referencesNumber.text = galleryValue.REF.toString()

            }
            parentFragmentManager.setFragmentResultListener("req",this){ key,bundle->
                val result = bundle.getString("data")
                val galleryValue = Gson().fromJson(result, CobroReq::class.java)
                var dataQR = generateQRCode(galleryValue.toJson())
                it.idIVQrcode.setImageBitmap(dataQR)
                // lanza mensaje nfc si está disponible
                /*if (nfcAdapter != null && nfcAdapter?.isEnabled == true) {
                    nfcMessage = galleryValue.toJson()
                    /* TODO implementar mensaje NFC para android <= 9
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        nfcAdapter.setNdefPushMessageCallback(this, requireActivity())
                    }*/
                }*/
            }
        }

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 600
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
        } catch (e: WriterException) {


        }
        return bitmap
    }


    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setTitle("Cobrar con CoDi")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }


    private fun compartirQR() {
        try {
            val ivImage = binding.idIVQrcode
            val bmpUri = getLocalBitmapUri(ivImage)
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
                                val shareIntent = Intent()
                                shareIntent.action = Intent.ACTION_SEND
                                shareIntent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    bitmap.mapToUri(requireContext())
                                )
                                shareIntent.type = "image/*"
                                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                requireContext().startActivity(Intent.createChooser(shareIntent, "Compartir QR"))
                            }
                        } else {
                            Log.e(TAG, "ERROR AL COMPARTIR LA IMAGEN")
                        }
                    }
                })
            }
        }
    }

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


    private fun requestPermissions(it:FragmentCobrarQrCodiBinding) {
        if (ContextCompat.checkSelfPermission(
               it.root.context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }else{

        }
        if (ContextCompat.checkSelfPermission(
                it.root.context,
                Manifest.permission.MANAGE_MEDIA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.MANAGE_MEDIA)
        }else{

        }
        if (ContextCompat.checkSelfPermission(
                it.root.context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }else{

        }
        if (ContextCompat.checkSelfPermission(
                it.root.context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }else{

        }
    }


    override fun onClickBackButton() {
        safeNavigate(
            R.id.action_cobrarQrCodi_to_codiModuleFragment,
            args = null,
            navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.action_cobrarQrCodi_to_codiModuleFragment, inclusive = true)
            }.build()
        )
    }

    /*@Deprecated("Deprecated in Java")
    override fun createNdefMessage(p0: NfcEvent?): NdefMessage {
        return NdefMessage(
            arrayOf(
                NdefRecord.createMime(
                    "application/spei-mobile-payment-request", nfcMessage.toByteArray()
                )
            )
        )
    }*/

}