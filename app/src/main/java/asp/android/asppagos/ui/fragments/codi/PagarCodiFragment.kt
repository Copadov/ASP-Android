package asp.android.asppagos.ui.fragments.codi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaConsecutivo
import asp.android.aspandroidcore.utils.generaHmacB64FromKey
import asp.android.aspandroidcore.utils.getFolioMensajeCobro
import asp.android.aspandroidcore.utils.sha512Hex
import asp.android.aspandroidcore.utils.xorHex
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
import asp.android.asppagos.databinding.FragmentPagarCodiBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.CobrarQrCodiViewModel
import asp.android.asppagos.ui.viewmodels.MainDashboardViewModel
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.time.Duration
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class PagarCodiFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name

    private var _binding: FragmentPagarCodiBinding? = null
    private val binding get() = _binding!!
    private var _data: CobrarQrCodiViewModel = CobrarQrCodiViewModel();
    private val data get() = _data!!;
    private var imageUri: Uri? = null
    private var cobro: CobroReq? = null
    private val codiAPI: CodiAPI by inject()
    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()

    val viewModel: MainDashboardViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagarCodiBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        findNavController().enableOnBackPressed(true);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        initViewModel()

        binding.let {
            it.scanLayout.setOnClickListener {
                safeNavigate(R.id.action_pagarCodiFragment_to_galleryCodiFragment)
            }
            it.galleryLayout.setOnClickListener {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)

                if (cobro != null) {
                    try {
                        validateCodiKeys(cobro!!)
                    } catch (e: Exception) {
                        Log.d(TAG, e.message!!)
                    }
                }
            }
            it.nfcLayout.setOnClickListener {
                CodiNFCHelpBottomSheetModal().show(
                    childFragmentManager,
                    CodiNFCHelpBottomSheetModal.TAG
                )
            }
        }
    }

    private fun setupToolbar() {
        this.binding.accountDetailToolbar.setTitle("Pagar con CoDi")
        this.binding.accountDetailToolbar.setASPMaterialToolbarsListeners(this)
    }


    override fun onClickBackButton() {
        findNavController().popBackStack()
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            try {
                imageUri = data?.data
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    this.context?.contentResolver, imageUri
                )

                val result = scanQRImage(bitmap)
                if (result.isNullOrBlank().not()) {
                    result?.let {
                        processQR(it)
                    }
                } else {
                    showCustomDialogError(
                        message1 = "Información",
                        message2 = "El codigo qr es incorrecto"
                    )
                }
            } catch (e: Exception) {
                showCustomDialogError(
                    message1 = "Información",
                    message2 = "El codigo qr es incorrecto"
                )
            }
        }
    }

    private fun initViewModel() {
        viewModel.nfcMessage.observe(viewLifecycleOwner) { msg ->
            processQR(msg)
        }
    }

    fun processQR(qrResult: String) {
        val cobroReq = Gson().fromJson(qrResult, CobroReq::class.java)
        if ((cobroReq is CobroReq)) {
            cobro = cobroReq
            Toast.makeText(this.context, "ESCANEADO", Toast.LENGTH_LONG).show()
            validateCodiKeys(cobro!!)
        } else {
            showCustomDialogError(
                message1 = "Información",
                message2 = "El codigo qr es incorrecto"
            )
        }
    }

    private fun validateCodiKeys(req: CobroReq) {
        val vendor = InfoPayment(
            nc = req.v?.DEV?.split("/").let {
                it?.first()
            },
            dv = req.v?.DEV?.split("/").let {
                it?.last()?.toInt()
            },
            ci = null,
            tc = null
        )
        val buyer = InfoPayment(
            nc = Prefs.get("aliasNC", "")!!,
            dv = Prefs.get("dv", "")?.toInt(),
            ci = null,
            tc = null
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
        Log.d(TAG, "$hmacCadena")

        val hmac = generaHmacB64FromKey(hmacCadena, Prefs.get(CODI_KEYSORUCE_KEY, ""))
        val solicitudDes = KeyDataRequest(
            type = 1,
            vendor = vendor,
            buyer = buyer,
            payment = paymentData,
            hmac = hmac
        )
        val d = "d=${solicitudDes.toJson()}"
        Log.d(TAG,"$d")
        codiApi().getKeyCif(Prefs[getString(R.string.codi_clave_descifrado)], d).enqueue(object : Callback<KeyDefData> {
            override fun onResponse(call: Call<KeyDefData>, response: Response<KeyDefData>) {
                val res = response.body()!!
                if (res.code == 0) {
                    req.ic.IDCN = idcn
                    generateProccessPay(res!!, req)
                } else {
                    //    dialog.dismiss()
                    val msg = when (res.code) {
                        -3 -> "No se puede pagar el mismo QR que generaste"
                        -8 -> "La cadena de validacion es incorrecta"
                        -4 -> "Saldo insuficiente"
                        -11 -> "La cuenta y el nombre del beneficiario no coinciden"
                        -18 -> "La fecha límite de pago caducó"
                        else -> "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde."
                    }

                    showCustomDialogError("Info", msg)
                }
            }

            override fun onFailure(call: Call<KeyDefData>, t: Throwable) {
                // dialog.dismiss()
                showCustomDialogError(message1 = "Información", message2 = "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde.")
            }

        })
    }


    private fun generateProccessPay(keyDefData: KeyDefData, cobroReq: CobroReq) {

        val cspcStr = cobroReq.ic?.IDCN + Prefs.get(CODI_KEYSORUCE_KEY, "") + cobroReq.ic?.SER
        val cspc = sha512Hex(cspcStr)
        val cspv = xorHex(keyDefData.emacCR!!, cspc!!)!!
        val decryptedData = desencriptaInformacionB64(cspv, cobroReq.ic?.ENC!!)
        Log.d(TAG, "DATA $decryptedData")
        val keypass = Prefs.get(CODI_KEY_P_KEY, "")!!
        val cspcStrASP = cobroReq.ic.IDCN + keypass + cobroReq.ic.SER
        val cspcASP = sha512Hex(cspcStrASP)
        val cspvASP = xorHex(keyDefData.emacCR, cspcASP!!)!!

        val cobroCifrado = CobroCifrado(
            ENC = encriptaInformacionB64(cspvASP, decryptedData!!),
            c = InfoPayment(
                cb = MainDashboardActivity.accountData.cuenta.clabe,
                dv = Prefs.get(CODI_DV_KEY, "").toInt(),
                nb = MainDashboardActivity.accountData.nombre,
                nc = Prefs.get(CODI_ALIAS_NC_KEY, ""), COM = 1
            ),
            emascCR = keyDefData.emacCR!!, IDCN = cobroReq.ic.IDCN, SER = cobroReq.ic?.SER
        )
        val registerPayment = RegisterPayment(
            encriptaInformacionB64(keypass, cobroCifrado.toJson()),
            MainDashboardActivity.accountData.cuenta.cuenta
        )

        val reqSend = registerPayment.encryptByGeneralKey()
        Log.d(TAG, "$reqSend")
        var webService = ""
        codiAspApi().registerPayment(reqSend){
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                // dialog.dismiss()
                Log.d(TAG, response.body()!!)
                val resp = response?.body()!!.decryptByGeneralKey<CommonServiceResponse>()
                Log.d(TAG, "${resp.toJson()}")
                if (resp?.code == 0) {
                    val registerPayment = resp?.data?.decryptByGeneralKey<RegisterPaymentResponse>()
                    Log.d(TAG, "${registerPayment.toJson()}")

                    parentFragmentManager.setFragmentResult(
                        "requestKey", bundleOf(
                            "data" to QRCode(
                                payment = Gson().fromJson(decryptedData, CobroRequest::class.java),
                                paymentCif = cobroReq,
                                responsePayment = registerPayment,
                                transaction = cobroCifrado
                            ).encryptByGeneralKey()
                        )
                    )
                    safeNavigate(R.id.action_pagarCodiFragment_to_detailPagarCodiFragment)
                    Toast.makeText(binding.root.context, "ESCANEADO", Toast.LENGTH_LONG).show()
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService,
                            AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                            response.body()
                        )
                    }
                } else {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService,
                            AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                            response.body()
                        )
                    }
                    val message = if (response.message().isNullOrEmpty()) {
                        "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde."
                    } else {
                        response.message()
                    }
                    showCustomDialogError(
                        message1 = "Info",
                        message2 = message
                    )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                GlobalScope.async {
                    aspTrackingRepository.consume(
                        webService,
                        AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                    )
                }
                showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
            }

        })

    }

    private fun codiApi(): CodiAPI {
        return codiAPI
    }


    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }


    private fun scanQRImage(bMap: Bitmap): String? {
        var contents: String? = null
        val intArray = IntArray(bMap.width * bMap.height)
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)
        val source: LuminanceSource = RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader: Reader = MultiFormatReader()
        try {
            val result: Result = reader.decode(bitmap)
            contents = result.getText()
        } catch (e: Exception) {
        }
        return contents
    }

}