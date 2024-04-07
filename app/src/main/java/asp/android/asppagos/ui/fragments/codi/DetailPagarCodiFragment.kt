package asp.android.asppagos.ui.fragments.codi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.widget.addTextChangedListener
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
import asp.android.asppagos.data.interfaces.CodiAspAPI
import asp.android.asppagos.data.models.codi.CobroCifrado
import asp.android.asppagos.data.models.codi.CobroReq
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.InfoPayment
import asp.android.asppagos.data.models.codi.KeyDataRequest
import asp.android.asppagos.data.models.codi.KeyDefData
import asp.android.asppagos.data.models.codi.PaymentData
import asp.android.asppagos.data.models.codi.ProcessPaymentReq
import asp.android.asppagos.data.models.codi.QRCode
import asp.android.asppagos.data.models.codi.RegisterPayment
import asp.android.asppagos.data.models.codi.RegisterPaymentResponse
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentDetailPagarCodiBinding
import asp.android.asppagos.network.interceptors.BasicAuthInterceptor
import asp.android.asppagos.network.interceptors.NetworkConnectionInterceptor
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.adapters.MovementType
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.formatAsMoney
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.mask
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.inject
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Currency
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class DetailPagarCodiFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name

    private var _binding: FragmentDetailPagarCodiBinding? = null
    private val binding get() = _binding!!
    private var cobroRequest: CobroRequest? = null
    private var cifCobro: CobroReq? = null
    private var qr: QRCode? = null
    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository: AspTrackingRepository by inject()

    private var cobro: CobroReq? = null
    private val codiAPI: CodiAPI by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailPagarCodiBinding.inflate(inflater, container, false)
        findNavController().enableOnBackPressed(true);

        return binding.root;
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        arguments?.let {
            // Si se trata de un request recibido por NFC, obtiene el mensaje de cobro
            val isNfcRequest = it.getBoolean("isNfcRequest", false)
            if (isNfcRequest) {
                val cobroMessage = it.getString("data", "")
                val cobroReq = Gson().fromJson(cobroMessage, CobroReq::class.java)

                if (cobroReq is CobroReq) {
                    cobro = cobroReq
                    validateCodiKeys(cobro!!)
                } else {
                    showCustomDialogError(
                        message1 = "Información",
                        message2 = "El codigo qr es incorrecto"
                    )
                }
            }
        }

        binding.confirmarBtnDetailPagar.setOnClickListener { btn ->
            if (btn.isEnabled) {

                val alert = AlertDialog.Builder(this.requireContext())
                    .setView(R.layout.pagar_qr_alert)
                    .setCancelable(true)
                    .create()
                alert.show()
                alert.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));
                val acceptBtn = alert.findViewById<Button>(R.id.doPaymentBtn)
                acceptBtn?.setOnClickListener {
                    GlobalScope.async {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                            ticket = "SOL_ENV_AP",
                            aditionalInfo = "ACEPTAR"
                        )
                    }
                    generarPagoCobro(cobroRequest!!, cifCobro!!, alert)
                }
                val cancelBtn = alert?.findViewById<Button>(R.id.cancelPaymentBtn)
                cancelBtn?.setOnClickListener {
                    GlobalScope.async {
                        aspTrackingRepository.inform(
                            eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                            ticket = "SOL_ENV_AP",
                            aditionalInfo = "RECHAZAR"
                        )
                    }
                    dialog.show(this.childFragmentManager, TAG)
                    generarPagoCobro(cobroRequest!!, cifCobro!!, alert, 2)
                }
            }
        }

        binding.editTxtAmount.addTextChangedListener { txt ->
            Log.d(TAG, txt.toString())
            if (!txt.isNullOrEmpty()) {
                cobroRequest?.AMO = if (txt.isEmpty() || txt.matches(
                        "[0-9]{1,13}(\\.[0-9]*)?"
                            .toRegex()
                    )
                ) {
                    txt.toString()
                        .replace("$", "")
                        .trim()
                        .toDouble()
                } else {
                    0.0
                }
            } else if (txt.isNullOrBlank()) {
                cobroRequest?.AMO = 0.0
            }
            validateBtn()
        }

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val result = bundle.getString("data")
            val data = result?.decryptByGeneralKey<QRCode>()
            setupView(data)
        }
    }

    private fun validateBtn() {
        binding.confirmarBtnDetailPagar.isEnabled = false
        if (binding.editTxtAmount.isEnabled) {
            cobroRequest?.AMO.let {
                if (it != null) {
                    binding.confirmarBtnDetailPagar.isEnabled = it > 0.0
                }
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

    private fun processPayment(cobroReq: QRCode, identifier: Int, alert: AlertDialog) {
        val keysource = Prefs.get(CODI_KEY_P_KEY, "")
        cobroReq.payment?.IDCN = cobroReq.responsePayment?.transactionFolio
        cobroReq.payment?.qrReader = Calendar.getInstance().timeInMillis
        Log.d(TAG, "${cobroReq.payment.toJson()}")
        val request = ProcessPaymentReq(
            data = encriptaInformacionB64(keysource, cobroReq?.payment.toJson()),
            identifier = identifier,
            account = MainDashboardActivity.accountData.cuenta.cuenta
        )
            .encryptByGeneralKey()
        var webService = ""
        codiAspApi().processPayment(request) {
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val result = response?.body()!!.decryptByGeneralKey<CommonServiceResponse>()
                    if (result?.code == 0) {
                        alert.dismiss()
                        dialog.dismiss()
                        val dataSend = CodiMovResponse(
                            folio = cobroReq.responsePayment?.transactionFolio!!,
                            owner = cobroReq.responsePayment?.owName,
                            reference = cobroReq.responsePayment?.reference!!,
                            amount = cobroReq.responsePayment?.amount,
                            incomeFrom = "qrRejected"
                        ).toJson()
                        parentFragmentManager.clearFragmentResult("requestKey")
                        parentFragmentManager.clearFragmentResultListener("requestKey")
                        parentFragmentManager.setFragmentResult(
                            "requestKey",
                            bundleOf("data" to dataSend)
                        )
                        safeNavigate(R.id.action_detailPagarCodiFragment_to_codiDevDetailFragment)
                    } else {
                        dialog.dismiss()
                        alert.dismiss()
                        val message = if (result?.message != null && result.message.isNotEmpty()) {
                            result.message
                        } else {
                            "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde."
                        }
                        showCustomDialogError("Info", message)
                    }
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService = webService,
                            typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                            response = response.body()
                        )
                    }
                } else {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService = webService,
                            typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                            response = response.body()
                        )
                    }
                    dialog.dismiss()
                    alert.dismiss()
                    showCustomDialogError("Info", "Tiempo de respuesta mayor")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                GlobalScope.async {
                    aspTrackingRepository.consume(
                        webService = webService,
                        typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                    )
                }
                dialog.dismiss()
                alert.dismiss()
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

    private fun generarPagoCobro(
        cobroR: CobroRequest,
        cifCobro: CobroReq,
        alert: AlertDialog,
        identifier: Int? = 0
    )
            : Boolean {
        var isSuccess = false
        if (identifier != 0) {
            processPayment(qr!!, 2, alert)
        } else {
            cobroR.AMO.let {
                isSuccess = (it != null && it > 0.0)
            }
            if (qr != null) {
                alert.dismiss()
                parentFragmentManager.setFragmentResult(
                    "data",
                    bundleOf("dataReq" to qr.encryptByGeneralKey())
                )
                safeNavigate(R.id.action_detailPagarCodiFragment_to_payQrConfirmFragment)
            }
        }
        return isSuccess
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
            nc = Prefs.get("aliasNC", ""),
            dv = Prefs.get("dv", "").toInt(),
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
        ) + req.ic.ENC!!
        Log.d(TAG, hmacCadena)

        val hmac = generaHmacB64FromKey(hmacCadena, Prefs.get(CODI_KEYSORUCE_KEY, ""))
        val solicitudDes = KeyDataRequest(
            type = 1,
            vendor = vendor,
            buyer = buyer,
            payment = paymentData,
            hmac = hmac
        )
        val d = "d=${solicitudDes.toJson()}"
        Log.d(TAG, d)
        codiApi().getKeyCif(Prefs[getString(R.string.codi_clave_descifrado)], d)
            .enqueue(object : Callback<KeyDefData> {
                override fun onResponse(call: Call<KeyDefData>, response: Response<KeyDefData>) {
                    val res = response.body()!!
                    if (res.code == 0) {
                        req.ic.IDCN = idcn
                        generateProccessPay(res, req)
                    } else {
                        //    dialog.dismiss()
                        showCustomDialogError("Info", "")
                    }
                }

                override fun onFailure(call: Call<KeyDefData>, t: Throwable) {
                    // dialog.dismiss()
                    showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
                }

            })
    }

    private fun generateProccessPay(keyDefData: KeyDefData, cobroReq: CobroReq) {

        val cspcStr = cobroReq.ic?.IDCN + Prefs.get(CODI_KEYSORUCE_KEY, "") + cobroReq.ic?.SER
        val cspc = sha512Hex(cspcStr)
        val cspv = xorHex(keyDefData.emacCR!!, cspc!!)!!
        val decryptedData = desencriptaInformacionB64(cspv, cobroReq.ic?.ENC!!)
        Log.d(TAG, "DATA $decryptedData")
        val keypass = Prefs.get(CODI_KEY_P_KEY, "")
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
            emascCR = keyDefData.emacCR, IDCN = cobroReq.ic.IDCN, SER = cobroReq.ic.SER
        )
        val registerPayment = RegisterPayment(
            encriptaInformacionB64(keypass, cobroCifrado.toJson()),
            MainDashboardActivity.accountData.cuenta.cuenta
        )

        val reqSend = registerPayment.encryptByGeneralKey()
        Log.d(TAG, "$reqSend")
        var webService = ""
        codiAspApi().registerPayment(reqSend) {
            webService = it
        }.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                // dialog.dismiss()
                Log.d(TAG, response.body()!!)
                val resp = response.body()!!.decryptByGeneralKey<CommonServiceResponse>()
                Log.d(TAG, resp.toJson())
                if (resp?.code == 0) {
                    val regPayment = resp.data?.decryptByGeneralKey<RegisterPaymentResponse>()
                    Log.d(TAG, regPayment.toJson())

                    val data = QRCode(
                        payment = Gson().fromJson(decryptedData, CobroRequest::class.java),
                        paymentCif = cobroReq,
                        responsePayment = regPayment,
                        transaction = cobroCifrado
                    )

                    setupView(data)

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
                    showCustomDialogError(
                        message1 = "Info",
                        message2 = response.message()
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

    private fun setupView(data: QRCode?) {
        binding.let {
            val cobroReq = data?.paymentCif
            val cifReq = data?.payment
            if (cifReq is CobroRequest && (cobroReq is CobroReq)) {
                qr = data
                it.detailNameScan.text = cifReq.v?.NAM
                it.accountNumberValue.text = cifReq.v?.ACC
                it.amountNumberValue.text = "Sin monto"
                cifReq.AMO.let { v ->
                    if (v != null) {
                        it.amountNumberValue.text =
                            v.toString().formatAsMoney(MovementType.TRANSFER)
                    }
                }
                it.conceptValue.text = cifReq.DES
                it.referencesValue.text = cifReq.REF.let { ref ->
                    if (ref != null && ref.toString() != "0") {
                        ref.toString()
                    } else {
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy"))
                    }
                }
                cifReq.IDCN = cifReq.IDC + getFolioMensajeCobro()
                cobroRequest = cifReq
                cifCobro = cobroReq
                cobroRequest?.AMO.let { a ->
                    when (a) {
                        null -> {

                            it.confirmarBtnDetailPagar.isEnabled = false
                            it.amountNumberValue.visibility = View.INVISIBLE
                            it.editTxtAmount.visibility = View.VISIBLE
                            it.editTxtAmount.isEnabled = true
                            cobroRequest?.AMO = 0.0

                        }

                        0.0 -> {
                            it.confirmarBtnDetailPagar.isEnabled = false
                            it.amountNumberValue.visibility = View.INVISIBLE
                            it.editTxtAmount.visibility = View.VISIBLE
                            it.editTxtAmount.isEnabled = true
                            cobroRequest?.AMO = 0.0
                        }

                        else -> {
                            it.confirmarBtnDetailPagar.isEnabled = true
                            it.amountNumberValue.visibility = View.VISIBLE
                            it.editTxtAmount.visibility = View.INVISIBLE
                            it.editTxtAmount.isEnabled = false
                            it.amountNumberValue.text = a.toString()
                            it.separatorAmount.layout(12, 0, 12, 0)
                        }
                    }
                }
            }
        }
    }
}