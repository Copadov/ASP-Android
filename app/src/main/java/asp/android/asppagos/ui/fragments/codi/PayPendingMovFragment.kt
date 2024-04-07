package asp.android.asppagos.ui.fragments.codi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.PinRequest
import asp.android.asppagos.data.models.codi.CobroRequest
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.ProcessPaymentReq
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.databinding.FragmentPayPendingMovBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.CODI_KEY_P_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByKeyPass
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN_REGISTER
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Fragment to handle Pin or Biometric authentication.
 * For CoDi pending movements.
 */
class PayPendingMovFragment : BaseFragment(), ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {
    override var TAG: String = this.javaClass.name
    private var _binding: FragmentPayPendingMovBinding? = null
    private val binding get() = _binding!!
    private val isBiometricActive = Prefs[PROPERTY_FINGER_TOKEN_REGISTER, false]
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val codiAspRepository: CodiAspRepository by inject()
    private val  aspTrackingRepository: AspTrackingRepository by inject()
    private var cobroReq: CobroRequest? = null
    private var codiMov: CodiMovResponse? = null
    private var keysource: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPayPendingMovBinding.inflate(inflater, container, false)
        findNavController().enableOnBackPressed(true);
        createBiometricPrompt()
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        keysource = Prefs[CODI_KEY_P_KEY, ""]
        parentFragmentManager.setFragmentResultListener("data", this) { key, bundle ->
            val result = bundle.getString("dataReq")
            val data = result?.decryptByGeneralKey<CobroRequest>()
            val movRespResult = bundle.getString("dataMovResp")
            val dataMov = movRespResult?.decryptByGeneralKey<CodiMovResponse>()
            if (data is CobroRequest) {
                cobroReq = data
            }
            if (dataMov is CodiMovResponse) {
                codiMov = dataMov
            }
        }
        binding.let {
            it.createQrPinValidator.addTextChangedListener { txt ->
                if (txt?.length == 4) {
                    it.btnContinueQr.isEnabled = true
                }
            }
            validateBiometric()
            it.btnContinueQr.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, TAG)
                if (validationPin()) {
                    dialog.dismiss()
                }
            }
        }
    }

    fun validationPin(): Boolean {
        val account =
            Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

        val request = PinRequest(
            biometrico = false,
            codigo = binding.createQrPinValidator.text.toString().encryptByKeyPass(),
            servicioId = 3,
            usuarioId = account.id
        )
        var webService = ""
        codiAspApi().pinValidation(request.encryptByGeneralKey()){
            webService
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        var res = response.body()?.decryptByGeneralKey<CommonServiceResponse>()
                        if (res?.code == 0) {
                            processPayment()
                        } else {
                            dialog.dismiss()
                            showCustomDialogError(
                                message1 = "Información",
                                message2 = res?.message!!
                            )
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
                    Log.d(TAG, "${t.stackTrace}")
                    dialog.dismiss()
                    showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
                }

            })
        return false
    }

    private fun codiAspApi(): CodiAspRepository {
        return codiAspRepository
    }


    private fun setupToolbar() {
        this.binding.toolbar.setTitle("Pagar con CoDi")
        this.binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    override fun onClickBackButton() {
        findNavController().popBackStack()
    }


    private fun validateBiometric() {
        if (isBiometricActive) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun createBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != 10) {
                        Toast.makeText(
                            requireContext(),
                            "Authentication error: $errString", Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    biometricSuccess()
                    dialog.show(requireActivity().supportFragmentManager, TAG)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Ingresar a ASP pago")
            .setSubtitle("Escanea tu huella para ingresar")
            .setNegativeButtonText("Usar contraseña")
            .build()
    }

    fun biometricSuccess() {
        processPayment()
    }

    private fun processPayment(identifier: Int? = 0) {
        dialog.show(childFragmentManager, TAG)

        val paymentRequest = ProcessPaymentReq(
            data = encriptaInformacionB64(keysource!!, cobroReq.toJson()),
            identifier = identifier,
            account = MainDashboardActivity.accountData.cuenta.cuenta
        )
        var webService = ""
        codiAspApi().processPayment(paymentRequest.encryptByGeneralKey()){
            webService = it
        }
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val dataResult = response?.body()!!
                        val result = if (dataResult.contains("{")) {
                            Gson().fromJson(dataResult, CommonServiceResponse::class.java)
                        } else {
                            dataResult.decryptByGeneralKey<CommonServiceResponse>()
                        }
                        if (result?.code == 0) {
                            dialog.dismiss()
                            result.data.let {
                                if (it != null) {
                                    val bd = it.decryptByGeneralKey<Map<String, Object>>()
                                    Log.d(TAG, "OBJECT RESPONSE : ${bd.toString()}")
                                    var dataSend = CodiMovResponse(
                                        folio = codiMov?.folio,
                                        owner = if (!bd.isNullOrEmpty()) {
                                            bd?.get("nombreBeneficiario") as String
                                        } else {
                                            codiMov?.vendor

                                        },
                                        reference = codiMov?.reference,
                                        amount = codiMov?.amount,
                                        incomeFrom = if (identifier == 0) {
                                            "qrPayment"
                                        } else {
                                            "qrRejected"
                                        },
                                        cr = if (!bd.isNullOrEmpty()) {
                                            bd?.get("claveRastreo") as String
                                        } else {
                                            codiMov?.cr
                                        }
                                    )
                                    parentFragmentManager.setFragmentResult(
                                        "requestKey",
                                        bundleOf("data" to dataSend.toJson())
                                    )
                                    safeNavigate(R.id.action_payPendingMovFragment_to_codiDevDetailFragment)
                                } else {
                                    var dataSend = CodiMovResponse(
                                        folio = codiMov?.folio,
                                        owner = codiMov?.vendor,
                                        reference = codiMov?.reference,
                                        amount = codiMov?.amount,
                                        incomeFrom = if (identifier == 0) {
                                            "qrPayment"
                                        } else {
                                            "qrRejected"
                                        },
                                        cr = codiMov?.cr
                                    )
                                    parentFragmentManager.setFragmentResult(
                                        "requestKey",
                                        bundleOf("data" to dataSend.toJson())
                                    )
                                    safeNavigate(R.id.action_payPendingMovFragment_to_codiDevDetailFragment)
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
                            val message = if (result?.message != null && result.message.isNotEmpty()) {
                                result.message
                            } else {
                                "Por el momento los servicios de Banco de México no están disponibles, por favor inténtalo más tarde."
                            }
                            showCustomDialogError("Info", message)
                        }
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
                    showCustomDialogError(message1 = "Información", message2 = "Ocurrió un error de comunicación, favor de intentar más tarde.")
                }

            })


    }

}