package asp.android.asppagos.ui.fragments.main


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaConsecutivo
import asp.android.aspandroidcore.utils.generaHmacB64FromKey
import asp.android.aspandroidcore.utils.generaKeySource
import asp.android.aspandroidcore.utils.sha512
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.BuildConfig
import asp.android.asppagos.R
import asp.android.asppagos.data.interfaces.CodiAPI
import asp.android.asppagos.data.models.codi.AccountCodiData
import asp.android.asppagos.data.models.codi.InformationDetail
import asp.android.asppagos.data.models.codi.RegistroInicialRequest
import asp.android.asppagos.data.models.codi.RegistroResponse
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.databinding.FragmentCoDiModuleBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.data.models.codi.DsData
import asp.android.asppagos.data.models.codi.GetStatusValidationRequest
import asp.android.asppagos.data.models.codi.GetStatusValidationResponse
import asp.android.asppagos.data.models.codi.RegData
import asp.android.asppagos.data.models.codi.CoDiASPRequest
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_ALIAS_KEY
import asp.android.asppagos.utils.CODI_ALIAS_NC_KEY
import asp.android.asppagos.utils.CODI_BENEFICIARIO_KEY
import asp.android.asppagos.utils.CODI_BENEFICIARIO_PAYMENT_NC_KEY
import asp.android.asppagos.utils.CODI_CR_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.CODI_ENROLLED_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.CODI_KEYSORUCE_PASS_KEY
import asp.android.asppagos.utils.CODI_KEY_KEY
import asp.android.asppagos.utils.CODI_TIMESTAMP_REGISTER_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.decryptByKeyPass
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogInfo
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class CoDiModuleFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private var _binding: FragmentCoDiModuleBinding? = null
    private val binding get() = _binding!!
    override var TAG: String = this.javaClass.name

    private var isRegister = false;
    private var keySource = String()

    private val timeToResendEnroll = 5 * 60 * 1000L // MINUTES * SECONDS * MILLIS


    private val codiAPI: CodiAPI by inject()

    private val codiAspRepository: CodiAspRepository by inject()
    private val aspTrackingRepository by inject<AspTrackingRepository>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoDiModuleBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar()
        isRegister = Prefs.get(CODI_ENROLLED_KEY, false)
        keySource = Prefs.get(CODI_KEYSORUCE_KEY, "")
        val pass = Prefs.get(CODI_KEYSORUCE_PASS_KEY, "").decryptByKeyPass()
        Prefs.set(
            CODI_KEY_KEY,
            generaKeySource(MainDashboardActivity.accountData.cuenta.cuenta, pass)!!
        )

        binding.let {
            it.codiCobrar.setOnClickListener {
                if (isRegister.not() && keySource.isEmpty() || checkTimePass()) {
                    Prefs.set(CODI_ENROLLED_KEY, false)
                    Prefs.set(CODI_KEYSORUCE_KEY, "")
                    showCodiRegister()
                } else {
                    validateAccountCodi(1, R.id.action_coDiModuleFragment_to_cobrarCodiFragment)
                }
            }
            it.codiPagar.setOnClickListener {
                if (isRegister.not() && keySource.isEmpty() || checkTimePass()) {
                    Prefs.set(CODI_ENROLLED_KEY, false)
                    Prefs.set(CODI_KEYSORUCE_KEY, "")
                    showCodiRegister()
                } else {
                    validateAccountCodi(1, R.id.action_coDiModuleFragment_to_pagarCodiFragment)
                }
            }
            it.codiMovimientos.setOnClickListener {
                if (isRegister.not() && keySource.isEmpty() || checkTimePass()) {
                    Prefs.set(CODI_ENROLLED_KEY, false)
                    Prefs.set(CODI_KEYSORUCE_KEY, "")
                    showCodiRegister()
                } else {
                    validateAccountCodi(1, R.id.action_coDiModuleFragment_to_codiMovFragment)
                }
                //findNavController().navigate(R.id.action_coDiModuleFragment_to_codiMovFragment)
            }
        }


        // Check if user enrolled
        if (isRegister.not() && keySource.isEmpty() || checkTimePass()) {
            showCodiRegister()
        }
    }

    private fun checkTimePass(): Boolean {
        if (isRegister && keySource.isNotEmpty()) return false
        val timestampToRegisterLong = Prefs.get(CODI_TIMESTAMP_REGISTER_KEY, 0L)
        if (timestampToRegisterLong == 0L) return true
        val timeToResendEnroll = timestampToRegisterLong + timeToResendEnroll
        val actualTime = Calendar.getInstance().time.time
        return actualTime >= timeToResendEnroll
    }

    private fun showCodiRegister() {
        val codiRegisterAlert = ASPMaterialDialogCustom.newInstance(
            title = "CoDi",
            subTitle = getString(R.string.codi_description),
            textOption1 = "cerrar",
            dialogType = ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
            visibleAcceptButton = true,
            optionType = 0,
            buttonTxt = "Registrarme"

        )
        codiRegisterAlert.isCancelable = false
        codiRegisterAlert.setASPMaterialDialogCustomListener(object :
            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
            override fun onClickAcceptButton(optionType: Int) {
                sendToApi(codiRegisterAlert)
            }

            override fun onClickClose() {
                codiRegisterAlert.dismiss()
                findNavController().popBackStack()
            }
        })
        codiRegisterAlert.show(childFragmentManager, ASPMaterialDialogCustom.TAG)
    }

    private fun validateAccountCodi(tp: Int, route: Int) {
        dialog.show(this.childFragmentManager, TAG)
        var nc = MainDashboardActivity.accountData
            .cuenta.telefono
        nc = if (tp == 1) {
            Prefs.get(CODI_ALIAS_NC_KEY, "")
        } else {
            MainDashboardActivity.accountData
                .cuenta.telefono
        }
        var result: Boolean? = false
        val cr = Prefs.get(CODI_CR_KEY, "").decryptByGeneralKeyOnlyText().replace("\"", "")

        val cadenaKeysouce = cr + Prefs.get(CODI_KEYSORUCE_KEY, "")

        val keysource = sha512(cadenaKeysouce)

        val cadenaHmac = nc + generaConsecutivo(Prefs.get(CODI_DV_KEY, "")) + cr

        val hmac = generaHmacB64FromKey(cadenaHmac, Prefs.get(CODI_KEYSORUCE_KEY, ""))

        val data = GetStatusValidationRequest(
            cr = cr,
            ds = DsData(
                nc = nc,
                dv = Prefs.get(CODI_DV_KEY, "").toInt()
            ),
            hmac = hmac
        )
        val send = "d=${data.toJson()}"
        codiApi().getStatusAccount(Prefs[getString(R.string.codi_consulta_validacion)], send)
            .enqueue(object : Callback<GetStatusValidationResponse> {
                override fun onResponse(
                    call: Call<GetStatusValidationResponse>,
                    response: Response<GetStatusValidationResponse>
                ) {
                    if (response.isSuccessful) {
                        dialog.dismiss()
                        if (tp == 0) {
                            safeNavigate(route)
                        } else {

                            val res = response.body()!!
                            if (res.code == 0) {
                                val dataDesf = desencriptaInformacionB64(keysource, res.data!!)
                                dataDesf.let {
                                    if (it != null) {
                                        val dataBenef =
                                            Gson().fromJson(it, AccountCodiData::class.java)
                                        if (dataBenef.rv == 1) {
                                            Log.d(TAG, " DATA : $it")
                                            if (tp == 1) {
                                                Prefs.set(
                                                    CODI_BENEFICIARIO_KEY,
                                                    dataBenef.encryptByGeneralKey()
                                                )
                                            } else {
                                                Prefs.set(
                                                    CODI_BENEFICIARIO_PAYMENT_NC_KEY,
                                                    dataBenef.encryptByGeneralKey()
                                                )
                                            }
                                            safeNavigate(route)
                                        } else {

                                            // Clean Data account
                                            val timeToResendEnroll = Prefs.get(CODI_TIMESTAMP_REGISTER_KEY, 0L) + timeToResendEnroll
                                            val actualTime = Calendar.getInstance().time.time
                                            if (actualTime >= timeToResendEnroll) {
                                                Prefs.set(CODI_ENROLLED_KEY, false)
                                                Prefs.set(CODI_KEYSORUCE_KEY, "")
                                            }

                                            showCustomDialogInfo(
                                                "Cuenta pendiente de validar",
                                                "Este proceso es externo a ASP y puede demorar unos minutos"
                                            )
                                        }
                                    }
                                }
                            } else {
                                dialog.dismiss()
                                showCustomDialogError("Info", getMessageValidate(res.code!!))
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetStatusValidationResponse>, t: Throwable) {
                    showCustomDialogError("Info", t.message!!)
                }

            })
    }

    private fun getMessageValidate(code: Int): String {
        return when (code) {
            -3 -> "Datos de consulta invalidos"
            -4 -> "Dispositivo no registrado"
            -5 -> "Ya cuentas con una solicitud en proceso"
            -7 -> "No existe información"
            -14 -> "Por el momento no es posible validar tu cuenta, intentalo más tarde"
            else -> "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde"
        }
    }


    private fun setToolBar() {
        binding.ASPMaterialToolbarMainDashboard.setTitle("CoDi")
        binding.ASPMaterialToolbarMainDashboard.setASPMaterialToolbarsListeners(this)
    }


    private fun getPhoneNumber(): String {
        return MainDashboardActivity.accountData.cuenta.telefono.replace("+52", "")
    }

    private fun codiApi(): CodiAPI {
        return codiAPI
    }

    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }

    @SuppressLint("HardwareIds")
    private fun sendToApi(alert: ASPMaterialDialogCustom): String {
        dialog.show(this.childFragmentManager, TAG)
        val codiApi = codiApi()
        val hardwareId =
            Settings.Secure.getString(
                binding.root.context?.contentResolver, Settings.Secure.ANDROID_ID
            )

        val idH = "${hardwareId}-${BuildConfig.APPLICATION_ID }"

        val informationDetail =
            InformationDetail("ANDROID", Build.VERSION.RELEASE, Build.MANUFACTURER, Build.MODEL)
        var epoc = Calendar.getInstance().timeInMillis

        val registroInicial = RegistroInicialRequest(
            getPhoneNumber().toLong(),
            idH,
            informationDetail,
            MainDashboardActivity.accountData.idCPU,
            epoc,
            sha512(epoc.toString() + MainDashboardActivity.accountData.idCPV)
        )
        var request = "d=${registroInicial.toJson()}"
        Log.d(TAG, request)

        codiApi.registroInicial(Prefs[getString(R.string.codi_registro_inicial)], request).enqueue(
            object : Callback<RegistroResponse> {
                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(
                    call: Call<RegistroResponse>,
                    response: Response<RegistroResponse>
                ) {
                    val res = response.body();
                    if (res?.edoPet == 0) {
                        Toast.makeText(
                            binding.root.context, "Se envio el sms", Toast.LENGTH_SHORT
                        ).show()
                        Prefs.set("gId", res.gId)
                        Prefs.set("dv", res.dv)
                        Prefs.set("alias", res.alias)
                        val register = RegData(
                            dv = res.dv.toInt(),
                            code = res.edoPet,
                            gId = res.gId,
                            ia = informationDetail,
                            idH = idH,
                            nc = getPhoneNumber(),
                            ncR = res.nc
                        )

                        // Save timpestamp when request send
                        Prefs.set(
                            CODI_TIMESTAMP_REGISTER_KEY,
                            Calendar.getInstance(Locale.getDefault()).time.time
                        )

                        registerASP(register, alert)
                    } else {
                        Prefs.set(CODI_KEYSORUCE_KEY, "")
                        dialog.dismiss()
                        showCustomDialogError("Info", getMessageValidate(res?.edoPet!!))
                    }
                    Log.d(TAG, "${Gson().toJson(response.code())}")
                    Log.d(TAG, "${response.body().toJson()}")
                }

                override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                    Toast.makeText(
                        binding.root.context,
                        "Error al enrolar cuenta sms",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, t.cause?.localizedMessage + "")
                    Log.d(TAG, "${t.message}")
                    dialog.dismiss()
                    showCustomDialogError("Info", "Error de Conexión")
                }

            })

        return ""
    }


    private fun registerASP(register: RegData, alert: ASPMaterialDialogCustom) {
        val passK = Prefs.get(CODI_KEY_KEY, "")
        val request = CoDiASPRequest(
            account = MainDashboardActivity.accountData.cuenta.cuenta,
            data = encriptaInformacionB64(passK, register.toJson())
        )
        var webService = ""
        codiAspApi().registroInicial(request.encryptByGeneralKey()) {
            webService = it
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        var bd = response.body()!!

                        val res = Gson().fromJson(bd, CommonServiceResponse::class.java)
                        if (res?.code == 0) {
                            dialog.dismiss()
                            alert.dismiss()
                            safeNavigate(R.id.action_coDiModuleFragment_to_codiConfirmCodeFragment)
                        } else {
                            dialog.dismiss()
                            showCustomDialogInfo("Info", res.message!!)
                        }
                        GlobalScope.async {
                            aspTrackingRepository.consume(
                                webService = webService,
                                typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                                response = bd
                            )
                        }
                    } else {
                        GlobalScope.async {
                            aspTrackingRepository.consume(
                                webService = webService,
                                typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                            )
                        }
                        dialog.dismiss()
                        showCustomDialogInfo("Info", "Error de conexión")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    GlobalScope.async {
                        aspTrackingRepository.consume(
                            webService = webService,
                            typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.SUCCESS,
                        )
                    }
                    dialog.dismiss()
                    showCustomDialogInfo("Info", t.message!!)
                }

            })
    }

    @Override
    override fun onClickBackButton() {
        findNavController().popBackStack()
    }
}