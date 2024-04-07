package asp.android.asppagos.ui.fragments.codi

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaConsecutivo
import asp.android.aspandroidcore.utils.generaHmacB64FromKey
import asp.android.aspandroidcore.utils.generaKeySource
import asp.android.aspandroidcore.utils.sha512
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.BuildConfig
import asp.android.asppagos.R
import asp.android.asppagos.data.events.SMSEvent
import asp.android.asppagos.data.interfaces.CodiAPI
import asp.android.asppagos.data.models.codi.CoDiASPRequest
import asp.android.asppagos.data.models.codi.CodiValidationCifRequest
import asp.android.asppagos.data.models.codi.CodiValidationReq
import asp.android.asppagos.data.models.codi.DataSolDetail
import asp.android.asppagos.data.models.codi.DsData
import asp.android.asppagos.data.models.codi.FirebaseData
import asp.android.asppagos.data.models.codi.InformationDetail
import asp.android.asppagos.data.models.codi.RegiSubRequest
import asp.android.asppagos.data.models.codi.RegistroResponse
import asp.android.asppagos.data.models.codi.ValidationRequest
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentCodiConfirmCodeBinding
import asp.android.asppagos.firebase.FirebaseInstanceServiceCodi
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_ALIAS_KEY
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_COD_R
import asp.android.asppagos.utils.CODI_CR_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_ENROLLED_KEY
import asp.android.asppagos.utils.CODI_G_ID_KEY
import asp.android.asppagos.utils.CODI_ID_HARDWARE
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.DEVICE_ID_UNIQUE_GUID
import asp.android.asppagos.utils.EncryptUtils
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.RxBus
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toEditable
import asp.android.asppagos.utils.toJson
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.Calendar
import kotlin.math.floor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CodiConfirmCodeFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name
    private var _binding: FragmentCodiConfirmCodeBinding? = null
    private val binding get() = _binding!!
    private val codiAPI: CodiAPI by inject()

    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCodiConfirmCodeBinding.inflate(inflater, container, false)
        return binding.root;
    }

    @SuppressLint("CheckResult", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var text = String()

        setupToolbar()

        binding.let {
            // Get screen width and height in PX
            var screenWidth = 0
            var screenHeight = 0

            if (Build.VERSION.SDK_INT in 26..29) {
                screenHeight = resources.displayMetrics.heightPixels
                screenWidth = resources.displayMetrics.widthPixels
            } else if (Build.VERSION.SDK_INT > 29) {
                val deviceWindowMetrics: WindowMetrics = requireContext().getSystemService(
                    WindowManager::class.java
                ).maximumWindowMetrics
                screenWidth = deviceWindowMetrics.bounds.width()
                screenHeight = deviceWindowMetrics.bounds.height()
            }

            // Available Width considering padding and margins
            val availableWidth = pxToDp(screenWidth) - 28

            // Percentage of available screen to use for PinView
            val pinViewTotalWidth = availableWidth * 0.9

            // Item width considering spacing between items and items count
            val pinViewItemWidth = (pinViewTotalWidth - 40) / 6

            // Item dimensions on Px
            val itemWidthPx = dpToPx(floor(pinViewItemWidth).toFloat()).toInt()
            val itemHeightPx = floor(itemWidthPx * 1.25).toInt()

            it.createCodiPinValidator.itemWidth = itemWidthPx
            it.createCodiPinValidator.itemHeight = itemHeightPx

            RxBus.listen(SMSEvent::class.java).subscribe { evt ->
                it.createCodiPinValidator.text =
                    evt.code.replace("\\D".toRegex(), "").toEditable()
            }
            it.createCodiPinValidator.addTextChangedListener { txt ->
                text = txt.toString();
                binding.btnContinueCodi.isEnabled = !txt.isNullOrBlank() && txt.length == 6
            }
            it.btnContinueCodi.let { i ->
                i.setOnClickListener {

                    // LOG USER SEND SMS EVENT
                    GlobalScope.async {
                        try {
                            aspTrackingRepository.inform(
                                eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                                ticket = "REG_INI",
                                aditionalInfo = "USR_INGRESO_SMS"
                            )
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }
                    }

                    if (!text.isNullOrBlank() && text.length == 6) {
                        dialog.show(parentFragmentManager, TAG)

                        val nc = MainDashboardActivity.accountData.cuenta.telefono
                        Log.d(TAG, text)
                        val hardwareId = Settings.Secure.getString(
                                binding.root.context?.contentResolver, Settings.Secure.ANDROID_ID
                            )

                        val idH = "${hardwareId}-${BuildConfig.APPLICATION_ID }"

                        val keySource = generaKeySource(text, idH, nc)

                        val gId = Prefs.get(CODI_G_ID_KEY, "")
                        val gIdDes = desencriptaInformacionB64(keySource!!, gId)
                        Log.d(TAG, "${gIdDes}")
                        if (!gIdDes.isNullOrBlank()) {
                            Prefs.set(CODI_COD_R, text) // Save CodR
                            Prefs.set(CODI_KEYSORUCE_KEY, keySource)
                            val aliasNC = desencriptaInformacionB64(
                                keySource.toString(), Prefs.get(CODI_ALIAS_KEY, "")
                            )
                            Log.d(TAG, "${aliasNC}")
                            Prefs.set(CODI_ALIAS_NC_KEY, aliasNC)
                            val instance = FirebaseInstanceServiceCodi.getInstance()
                            instance.initFirebase(
                                FirebaseData(
                                    gId = gIdDes,
                                    apiKey = when (BuildConfig.FLAVOR) {
                                        "DevBuild" -> "AlzaSyA9_sZ4SvNtl-_XNC4Q0yzN8nTovXX3sg0"
                                        else -> "AlzaSyC162PF98EvaTA-TWIjDgVjQzM3VEZVIrl"
                                    },
                                    aId = when (BuildConfig.FLAVOR) {
                                        "DevBuild" -> "b63c1d8b40c8b20c"
                                        else -> "b63c1d8b40c8b20c"
                                    },
                                    pId = when (BuildConfig.FLAVOR) {
                                        "DevBuild" -> "cobrospei-beta5"
                                        else -> "codi-productivo4"
                                    }
                                ), super.requireActivity().applicationContext!!
                            )
                            val app1 = instance.getFirebaseInstance()


                            app1.get(FirebaseMessaging::class.java).token.addOnCompleteListener { token ->

                                GlobalScope.async {
                                    try {
                                        aspTrackingRepository.inform(
                                            eventAction = AspTrackingRepositoryImpl.EventAction.WEB_SERVICE,
                                            ticket = "REG_SUB",
                                            aditionalInfo = "CONSUMO_API_FIREBASE"
                                        )
                                    } catch (exception: Exception) {
                                        exception.printStackTrace()
                                    }
                                }

                                if (!token.result.isNullOrBlank()) {

                                    val informationDetail = InformationDetail(
                                        "ANDROID",
                                        Build.VERSION.RELEASE,
                                        Build.MANUFACTURER,
                                        Build.MODEL
                                    )
                                    val tk: String = token.result!!
                                    //  FirebaseApp.getInstance("banxico").let {
                                    if (it != null) {
                                        // msg.token.addOnCompleteListener { comp->
                                        // if(comp.result!=null&&(!comp.result!!.isNullOrBlank())){
                                        val consecutivo =
                                            generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))!!

                                        val cadenaHmac = aliasNC + consecutivo + tk

                                        var epoc = Calendar.getInstance().timeInMillis

                                        val hmac = generaHmacB64FromKey(cadenaHmac, keySource!!)
                                        // Here save HMAC
                                        val hash =
                                            sha512(epoc.toString() + MainDashboardActivity.accountData.idCPV)

                                        val requestSub = RegiSubRequest(
                                            Prefs.get(CODI_ALIAS_NC_KEY, ""),
                                            consecutivo.toInt(),
                                            idH,
                                            informationDetail,
                                            tk,
                                            hmac!!,
                                            MainDashboardActivity.accountData.idCPU,
                                            epoc,
                                            hash
                                        )

                                        Log.d(TAG, "${requestSub.toJson()}")

                                        var request = "d=${requestSub.toJson()}"
                                        codiApi().registerSub(
                                            Prefs[getString(R.string.codi_registro_subsecuente)],
                                            request
                                        ).enqueue(object :
                                            Callback<RegistroResponse> {
                                            @SuppressLint("SuspiciousIndentation")
                                            override fun onResponse(
                                                call: Call<RegistroResponse>,
                                                response: Response<RegistroResponse>
                                            ) {

                                                if (response.body()?.edoPet == 0) {
                                                    Toast.makeText(
                                                        binding.root.context,
                                                        "Validando la cuenta espere",
                                                        Toast.LENGTH_SHORT
                                                    )

                                                    val cuenta =
                                                        MainDashboardActivity.accountData.cuenta.clabe

                                                    val strHmac =
                                                        aliasNC + consecutivo + cuenta + "40" + "90659"

                                                    val hmac =
                                                        generaHmacB64FromKey(strHmac, keySource)

                                                    val account = ValidationRequest(
                                                        cuenta, 40, 90659,
                                                        DataSolDetail(
                                                            aliasNC!!,
                                                            response.body()?.dv?.toInt()!!
                                                        ), hmac!!
                                                    )
                                                    Log.d(TAG, "${account.toJson()}")
                                                    validationAccount(
                                                        "d=${account.toJson()}",
                                                        requestSub,
                                                        hmac
                                                    )

                                                    // Save hardware id
                                                    Prefs.set(DEVICE_ID_UNIQUE_GUID, idH)

                                                    // Update Initial data
                                                    EncryptUtils.updateInitialData()

                                                } else {
                                                    dialog.dismiss()
                                                    showCustomDialogError(
                                                        "info",
                                                        "Ya cuentas con una solicitud en proceso"
                                                    )
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<RegistroResponse>,
                                                t: Throwable
                                            ) {
                                                dialog.dismiss()
                                                showCustomDialogError(
                                                    "Info",
                                                    "Error al enrolar la cuenta"
                                                )
                                            }

                                        })
                                        //}
                                        // }
                                        //}
                                    }


                                }
                            }
                        } else {
                            Prefs.set(CODI_KEYSORUCE_KEY, "")
                            dialog.dismiss()
                            showCustomDialogError(
                                message1 = "Información",
                                message2 = "El codigo proporcionado incorrecto"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle(getString(R.string.code_codi_confirm_toolbar))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }


    /**
     * Method to convert pixels unit to equivalent dp, depending on device density.
     *
     * @param px A value in pixels.
     * @return A float value to represent dp equivalent to px.
     */
    private fun pxToDp(px: Int): Float {
        return px / (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * Method to convert dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp.
     * @return A float value to represent px equivalent to dp.
     */
    private fun dpToPx(dp: Float): Float {
        return dp * (requireContext().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun validationAccount(data: String, ia: RegiSubRequest, hmac: String) {
        //Log.d("JHMM", "RegiSubRequest: ${ia}")
        codiApi().validationAccount(Prefs[getString(R.string.codi_validacion_cuenta)], data)
            .enqueue(object : Callback<RegistroResponse> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(
                    call: Call<RegistroResponse>,
                    response: Response<RegistroResponse>
                ) {
                    val res = response.body();
                    Log.d(TAG, "${res.toJson()}")
                    if (res?.edoPet == 0) {
                        Prefs.set(CODI_CR_KEY, res?.cr.encryptByGeneralKey())

                        val consecutivo = generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))

                        val cadenaHmac = Prefs.get(CODI_ALIAS_NC_KEY, "") + consecutivo + res?.cr

                        val hmac = generaHmacB64FromKey(cadenaHmac, Prefs.get("KeySourceCodi"))

                        Log.d(TAG, "$hmac")
                        Log.d(TAG, "${Prefs.get(CODI_ALIAS_NC_KEY, "")}")
                        val requestReq = RegisterSubRequest(
                            codeCodi = binding.createCodiPinValidator.text.toString(),
                            dv = Prefs.get(CODI_DV_KEY, "").toInt(),
                            dvOm = res.dvOmision,
                            dvR = res.dvOmision,
                            hmac = hmac,
                            ia = ia.ia,
                            idH = ia.idH,
                            idN = ia.idN,
                            nc = ia.nc
                        )
                        Log.d(TAG, requestReq.toJson())
                        registerSubseq(requestReq)

                    } else {
                        Prefs.set(CODI_KEYSORUCE_KEY, "")
                        dialog.dismiss()
                        showCustomDialogError(
                            "Info",
                            "Ya cuentas con una solicitud en proceso"
                        )
                    }
                    Log.d(TAG, "${Gson().toJson(response.code())}")
                    Log.d(TAG, "${response.body().toJson()}")
                }

                override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                    dialog.dismiss()
                    showCustomDialogError("Info", "Error de conexión")
                }

            })
    }

    private fun validationAccountAsp() {
        val dv = Prefs.get(CODI_DV_KEY, "")?.toInt()
        val passKey = Prefs.get(CODI_KEY_P_KEY, "")!!
        val nc = Prefs.get(CODI_ALIAS_NC_KEY, "")
        val consecutivo = generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))
        val cadenaHmac = Prefs.get(CODI_ALIAS_NC_KEY, "") + consecutivo + MainDashboardActivity
            .accountData.cuenta.clabe + 40 + 90659
        val hmac = generaHmacB64FromKey(cadenaHmac, Prefs.get(CODI_KEYSORUCE_KEY))

        val codiData = CodiValidationReq(
            cb = MainDashboardActivity.accountData.cuenta.clabe,
            cr = Prefs.get(CODI_CR_KEY, "").decryptByGeneralKeyOnlyText().replace("\"", ""),
            ds = DsData(
                dv = dv,
                nc = nc
            ),
            hmac = hmac, tyc = null
        )

        Log.d(TAG, codiData.toJson())
        val codiValidationRequest = CodiValidationCifRequest(
            account = MainDashboardActivity.accountData.cuenta.cuenta,
            data = encriptaInformacionB64(passKey, codiData.toJson())
        )
        var webService = ""
        codiAspApi().accountValidation(codiValidationRequest.encryptByGeneralKey()) {
            webService = it
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val dataRes = response?.body()!!
                        val res = if (dataRes.contains("{")) {
                            Gson().fromJson(dataRes, CommonServiceResponse::class.java)
                        } else {
                            dataRes.decryptByGeneralKey<CommonServiceResponse>()
                        }
                        if (res?.code == 0) {
                            res.data.let {
                                if (it != null && it.contains("{")) {
                                    Log.d(TAG, "$it")
                                    validationPaymentAccount()
                                } else {
                                    val r = desencriptaInformacionB64(passKey, res.data!!)
                                    Log.d(TAG, "$r")
                                    validationPaymentAccount()
                                }
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
                            val message = if (res?.message != null && res.message.isNotEmpty()) {
                                res.message
                            } else {
                                "Ya cuentas con una solicitud en proceso"
                            }
                            showCustomDialogError("Info", message)
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService,
                            AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR
                        )
                    }
                    dialog.dismiss()
                    showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
                }

            })
    }

    fun registerSubseq(request: RegisterSubRequest) {
        val passKey = Prefs.get(CODI_KEY_P_KEY, "")
        val req = CoDiASPRequest(
            data = encriptaInformacionB64(passKey, request.toJson()),
            account = MainDashboardActivity.accountData.cuenta.cuenta
        )
        var webService = ""
        codiAspApi().registroSubsequent(req.encryptByGeneralKey()) {
            webService = it
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val dataRes = response?.body()!!
                        val res = if (dataRes.contains("{")) {
                            Gson().fromJson(dataRes, CommonServiceResponse::class.java)
                        } else {
                            dataRes.decryptByGeneralKey<CommonServiceResponse>()
                        }
                        if (res?.code == 0) {
                            validationAccountAsp()
                        } else {
                            val message = if (res?.message != null && res.message.isNotEmpty()) {
                                res.message
                            } else {
                                "Ya cuentas con una solicitud en proceso"
                            }
                            dialog.dismiss()
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
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService = webService,
                            typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                        )
                    }
                    dialog.dismiss()
                    showCustomDialogError("Info", "Error de conexión")
                }

            })
    }

    fun validationPaymentAccount() {

        val dv = Prefs.get(CODI_DV_KEY, "").toInt()
        val passKey = Prefs.get(CODI_KEY_P_KEY, "")
        val nc = MainDashboardActivity.accountData.cuenta.telefono
        val consecutivo = generaConsecutivo(Prefs.get(CODI_DV_KEY, ""))
        val cadenaHmac =
            nc + consecutivo + MainDashboardActivity.accountData.cuenta.clabe + 40 + 90659
        val hmac = generaHmacB64FromKey(cadenaHmac, Prefs.get(CODI_KEYSORUCE_KEY))
        val validationPayment = CodiValidationReq(
            cb = MainDashboardActivity.accountData.cuenta.clabe,
            concept = "",
            cr = "N/A-CUENTA PARA REALIZAR PAGOS-" + Calendar.getInstance().timeInMillis,
            ds = DsData(
                dv = dv,
                nc = nc
            ),
            hmac = hmac, tyc = 2
        )
        Log.d(TAG, validationPayment.toJson())
        val requestPaymentValidation = CodiValidationCifRequest(
            account = MainDashboardActivity.accountData.cuenta.cuenta,
            data = encriptaInformacionB64(passKey, validationPayment.toJson())
        )
        var webService = ""
        codiAspApi().accountValidation(requestPaymentValidation.encryptByGeneralKey()) {
            webService = it
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {


                        val dataRes = response?.body()!!
                        val res = if (dataRes.contains("{")) {
                            Gson().fromJson(dataRes, CommonServiceResponse::class.java)
                        } else {
                            dataRes.decryptByGeneralKey<CommonServiceResponse>()
                        }
                        if (res?.code == 0) {
                            val r = desencriptaInformacionB64(passKey, res.data!!)
                            Log.d(TAG, "$r")
                            Prefs.set(CODI_ENROLLED_KEY, true) // User is enrolled
                            dialog.dismiss()
                            safeNavigate(R.id.action_codiConfirmCodeFragment_to_coDiModuleFragment)
                        } else {
                            val message = if (res?.message != null && res.message.isNotEmpty()) {
                                res.message
                            } else {
                                "Ya cuentas con una solicitud en proceso"
                            }
                            dialog.dismiss()
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
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService = webService,
                            typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                        )
                    }
                    dialog.dismiss()
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

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }

}