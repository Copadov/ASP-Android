package asp.android.asppagos.ui.fragments.codi


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaConsecutivo
import asp.android.aspandroidcore.utils.generaHmacB64FromKey
import asp.android.aspandroidcore.utils.getFolioMensajeCobro
import asp.android.aspandroidcore.utils.sha512Hex
import asp.android.aspandroidcore.utils.xorHex
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.interfaces.CodiAPI
import asp.android.asppagos.data.models.codi.CobroCifrado
import asp.android.asppagos.data.models.codi.CobroReq
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.data.models.codi.InfoPayment
import asp.android.asppagos.data.models.codi.KeyDataRequest
import asp.android.asppagos.data.models.codi.KeyDefData
import asp.android.asppagos.data.models.codi.PaymentData
import asp.android.asppagos.data.models.codi.QRCode
import asp.android.asppagos.data.models.codi.RegisterPayment
import asp.android.asppagos.data.models.codi.RegisterPaymentResponse
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentGalleryCodiBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GalleryCodiFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name
    private var _binding: FragmentGalleryCodiBinding? = null
    private val binding get() = _binding!!
    private var isSuccessScan: Boolean = false
    private val codiAPI: CodiAPI by inject()
    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            runQRcodeReader()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryCodiBinding.inflate(inflater, container, false)

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        binding.let {
            if (ContextCompat.checkSelfPermission(
                    it.root.context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestPermission.launch(Manifest.permission.CAMERA)
                } else {
                    showSettingsDialog()
                }
            } else {
                runQRcodeReader()
            }
            it.capturarBtnQR.setOnClickListener { i ->
                if (ContextCompat.checkSelfPermission(
                        i.context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        requestPermission.launch(Manifest.permission.CAMERA)
                    } else {
                        showSettingsDialog()
                    }
                } else {
                    runQRcodeReader()
                }


            }


        }


    }


    private fun generateQRCode(text: String): Bitmap {
        val width = 650
        val height = 650
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
        this.binding.accountDetailToolbar.setTitle("Pagar con CoDi")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }


    override fun onClickBackButton() {
        findNavController().popBackStack()
    }

    private fun runQRcodeReader() {
        val scanOptions = ScanOptions().setOrientationLocked(false)
        scanOptions.setBarcodeImageEnabled(false)
        scanOptions.setBeepEnabled(false)
        scanOptions.setPrompt("Escanee el QR")
        barcodeLauncher.launch(scanOptions)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Toast.makeText(this.context, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(
                    this.context,
                    "Cancelled due to missing camera permission",
                    Toast.LENGTH_LONG
                ).show()
            }
            findNavController().popBackStack()
        } else {
            try {
                Log.d(TAG, "${result.contents}")
                var data = Gson().fromJson(result.contents, CobroReq::class.java)
                Log.d(TAG, "${data.toJson()}")
                dialog.show(this.parentFragmentManager, TAG)
                validateCodiKeys(data)
            } catch (e: Exception) {
                findNavController().popBackStack()
                showCustomDialogInfo("Info", "Favor de capturar nuevamente el codigo QR")
            }
        }
    }


    private fun validateCodiKeys(req: CobroReq) {
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, TAG)
        }
        val vendor = InfoPayment(
            nc = req.v?.DEV?.split("/").let {
                it?.first()
            },
            dv = req.v?.DEV?.split("/").let {
                it?.last()?.toInt()
            }, tc = null, ci = null
        )
        val buyer = InfoPayment(
            nc = Prefs.get(CODI_ALIAS_NC_KEY, "")!!,
            dv = Prefs.get(CODI_DV_KEY, "")?.toInt(),
            tc = null,
            ci = null
        )
        val idcn = req.ic?.IDC + getFolioMensajeCobro()
        val paymentData = PaymentData(
            id = idcn,
            s = req.ic?.SER,
            mc = req.ic?.ENC!!,
            idcn = null
        )

        val hmacCadena = paymentData.id + req.ic?.SER
            .toString() + vendor.nc + generaConsecutivo(
            vendor.dv?.toString()!!
        ) + Prefs.get(CODI_ALIAS_NC_KEY, "") + generaConsecutivo(
            Prefs.get(CODI_DV_KEY, "")
        ) + req.ic?.ENC!!

        val hmac = generaHmacB64FromKey(hmacCadena, Prefs.get(CODI_KEYSORUCE_KEY, ""))

        val solicitudDes = KeyDataRequest(
            type = 1,
            vendor = vendor,
            buyer = buyer,
            payment = paymentData,
            hmac = hmac
        )

        codiApi().getKeyCif(Prefs[getString(R.string.codi_clave_descifrado)], "d=${solicitudDes.toJson()}").enqueue(object :Callback<KeyDefData>{
            override fun onResponse(call: Call<KeyDefData>, response: Response<KeyDefData>) {
                val res = response.body()!!
                if (res.code == 0) {

                    req.ic.IDCN = paymentData.id
                    val cspcStr = paymentData.id + Prefs.get(CODI_KEYSORUCE_KEY, "") + req.ic?.SER
                    val cspc = sha512Hex(cspcStr)
                    val cspv = xorHex(res.emacCR!!, cspc!!)
                    val decryptedData = desencriptaInformacionB64(cspv!!, req.ic?.ENC!!)
                    Log.d(TAG, "DATA $decryptedData")
                    val keypass = Prefs.get(CODI_KEY_P_KEY, "")!!
                    val cspcStrASP = req.ic.IDCN + keypass + req.ic.SER
                    val cspcASP = sha512Hex(cspcStrASP)
                    val cspvASP = xorHex(res.emacCR, cspcASP!!)!!

                    val cobroCifrado = CobroCifrado(
                        ENC = encriptaInformacionB64(cspvASP, decryptedData!!),
                        c = InfoPayment(
                            cb = MainDashboardActivity.accountData.cuenta.clabe,
                            dv = Prefs.get(CODI_DV_KEY, "").toInt(),
                            nb = MainDashboardActivity.accountData.nombre,
                            nc = Prefs.get(CODI_ALIAS_NC_KEY, ""),
                            COM = 1
                        ),
                        emascCR = res.emacCR,
                        IDCN = req.ic.IDCN,
                        SER = req.ic?.SER
                    )
                    val registerPayment = RegisterPayment(
                        encriptaInformacionB64(keypass, cobroCifrado.toJson()),
                        MainDashboardActivity.accountData.cuenta.cuenta
                    )
                    val reqSend = registerPayment.encryptByGeneralKey()
                    Log.d(TAG, "$reqSend")
                    var webservice = ""
                    codiAspApi().registerPayment(reqSend){
                        webservice = it
                    }.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            dialog.dismiss()
                            Log.d(TAG, response.body()!!)
                            val resp =
                                response?.body()!!.decryptByGeneralKey<CommonServiceResponse>()
                            Log.d(TAG, "${resp.toJson()}")
                            if (resp?.code == 0) {
                                val registerPayment =
                                    resp?.data?.decryptByGeneralKey<RegisterPaymentResponse>()
                                Log.d(TAG, "${registerPayment.toJson()}")
                                isSuccessScan = true
                                binding.qrCaptureImg.setImageBitmap(generateQRCode(req.toJson()))
                                parentFragmentManager.setFragmentResult(
                                    "requestKey", bundleOf(
                                        "data" to QRCode(
                                            payment = Gson().fromJson(
                                                decryptedData,
                                                CobroRequest::class.java
                                            ),
                                            paymentCif = req,
                                            responsePayment = registerPayment,
                                            transaction = cobroCifrado
                                        ).encryptByGeneralKey()
                                    )
                                )
                                if (dialog.isVisible) {
                                    dialog.dismiss()
                                }
                                Toast.makeText(binding.root.context, "ESCANEADO", Toast.LENGTH_LONG)
                                    .show()
                                binding.confirmarBtnQR.isEnabled = true
                                safeNavigate(R.id.action_galleryCodiFragment_to_detailPagarCodiFragment)
                                GlobalScope.async {
                                    aspTrackingRepository.consume(
                                        webService = webservice,
                                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                                        response = response.body()
                                    )
                                }

                            } else {
                                GlobalScope.async {
                                    aspTrackingRepository.consume(
                                        webService = webservice,
                                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                                    )
                                }
                                showCustomDialogError(
                                    message1 = "Info",
                                    message2 = response.message()
                                )
                                findNavController().popBackStack()
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            GlobalScope.async {
                                aspTrackingRepository.consume(
                                    webService = webservice,
                                    typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                                )
                            }
                            showCustomDialogError(message1 = "Información", message2 = "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde.")
                            findNavController().popBackStack()
                        }

                    })
                    //generateProccessPay(res,req)
                } else {
                    dialog.dismiss()
                    var msg = when (res.code) {
                        -3 -> "No se puede pagar el mismo QR que generaste"
                        -8 -> "La cadena de validacion es incorrecta"
                        -4 -> "Saldo insuficiente"
                        -11 -> "La cuenta y el nombre del beneficiario no coinciden"
                        -18 -> "La fecha límite de pago caducó"
                        else -> "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde."
                    }
                    showCustomDialogError("Info", msg)
                    findNavController().popBackStack()
                }
            }

            override fun onFailure(call: Call<KeyDefData>, t: Throwable) {
                Log.d(TAG, t.stackTraceToString())
                showCustomDialogError(message1 = "Información", message2 = "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde.")
                findNavController().popBackStack()
            }

        })

    }


    private fun codiApi(): CodiAPI {
        return codiAPI
    }


    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }

    /**
     * Show dialog to navigate to app settings page.
     */
    private fun showSettingsDialog() {
        val settingsDialog = ASPMaterialDialogCustom.newInstance(
            requireContext().getString(R.string.information_dialog_text),
            requireContext().getString(R.string.dialog_camera_permission),
            "aceptar",
            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            false
        )

        settingsDialog.setASPMaterialDialogCustomListener(object : ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                settingsDialog.dismiss()
            }

            override fun onClickClose() {
                settingsDialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }

        })

        settingsDialog.show(childFragmentManager,
            ASPMaterialDialogCustom.TAG)
    }

}