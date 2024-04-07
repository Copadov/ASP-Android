package asp.android.asppagos.ui.fragments.onboarding

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.BuildConfig
import asp.android.asppagos.MainApplication
import asp.android.asppagos.R
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.modules.createAppCodiModules
import asp.android.asppagos.databinding.FragmentRegisterInfoBinding
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.LoginViewModel
import asp.android.asppagos.utils.*
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.context.loadKoinModules
import java.util.concurrent.Executor

class RegisterInfo : BaseFragment(),
    ASPTMaterialToolbar.ASPMaterialToolbarsListeners {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var _binding: FragmentRegisterInfoBinding? = null
    private val binding get() = _binding!!
    val viewModel: LoginViewModel by activityViewModel()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                    viewModel.setPassword(Prefs[PROPERTY_PASSWORD_REGISTER, ""])
                    viewModel.login()

                    dialog.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )

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

    private fun launchDashboardActivity() {
        requireActivity().finish()
        startActivity(
            Intent(
                requireContext(),
                MainDashboardActivity::class.java
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkDeviceHasBiometric()

        binding.let {

            it.versionAppText.text =
                getString(
                    R.string.version_app_text_info,
                    Prefs[VERSION_APP, ""]
                )

            if (Prefs.get(PROPERTY_REGISTER_SUCCESS)) {

                viewModel.setPhone(Prefs.get(PROPERTY_PHONE_USER_LOGGED, ""))

                it.textViewTitle.text = getString(
                    R.string.register_infor_text_title_greetings_user_logged,
                    Prefs.get(PROPERTY_NAME_USER_LOGGED, "").split(" ")[0]
                )

                it.toolbarRegisterInfo.hideBackIcon()
            }

            val existRegisterAfterOnboarding =
                Prefs[PROPERTY_FINGER_TOKEN_REGISTER, false]

            it.buttonValidation.isVisible = existRegisterAfterOnboarding

            it.inputPass.requestFocus()
            it.inputPass.showKeyboard()

            it.toolbarRegisterInfo.setASPMaterialToolbarsListeners(this)

            it.buttonValidation.setOnClickListener {
                validateBiometrics()
            }

            it.buttonLoginAccess.setOnClickListener {
                requireActivity().hideKeyboard()
                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )
                viewModel.setPassword(binding.inputPass.text.toString())
                viewModel.login()
            }
            it.inputPass.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.buttonLoginAccess.isEnabled =
                        p0!!.isNotEmpty()
                                && p0.length >= 6
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

            it.recoverOption.setOnClickListener {
                safeNavigate(R.id.action_registerInfo_to_recoverPassFragment)
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {

            it.loginDataResponse.observe(viewLifecycleOwner) { loginDataResponse ->

                val loginResponse =
                    Gson().fromJson<LoginResponseData>(
                        decryptDataWithAccess(
                            loginDataResponse.data,
                            viewModel.phone.value!!,
                            encriptPassword(viewModel.password.value!!)
                        )
                    )

                Prefs.set(
                    PROPERTY_REGISTER_SUCCESS,
                    true
                )
                Prefs.set(
                    PROPERTY_NAME_USER_LOGGED,
                    loginResponse.nombre
                )
                Prefs.set(
                    PROPERTY_PHONE_USER_LOGGED,
                    viewModel.phone.value!!
                )
                Prefs.set(
                    PROPERTY_CODI_ENCRIPTED,
                    encriptData(loginResponse.urlCoDi)
                )
                Prefs.set(
                    PROPERTY_ASP_ENCRIPTED,
                    encriptData(loginResponse.urlAsp)
                )

                Prefs.set(
                    PROPERTY_ACCOUNT_ENCRIPTED,
                    encriptData(loginResponse)
                )

                val codiUrls = loginResponse.urlCoDi

                // Guarda endpoints de servicios de codi en preferencias
                codiUrls.firstOrNull { item -> item.clave == CODI_BASE_URL }
                    ?.let { codiUrl ->
                        Prefs.set(CODI_BASE_URL, encriptData(codiUrl.url))
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_registro_inicial)}
                    ?.let { registroInicialUrl ->
                        Prefs.set(getString(R.string.codi_registro_inicial), registroInicialUrl.url)
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_registro_subsecuente)}
                    ?.let { registroSubUrl ->
                        Prefs.set(getString(R.string.codi_registro_subsecuente), registroSubUrl.url)
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_validacion_cuenta)}
                    ?.let { validacionUrl ->
                        Prefs.set(getString(R.string.codi_validacion_cuenta), validacionUrl.url)
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_clave_descifrado)}
                    ?.let { claveDesUrl ->
                        Prefs.set(getString(R.string.codi_clave_descifrado), claveDesUrl.url)
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_consulta_validacion)}
                    ?.let { consultaValidacionUrl ->
                        Prefs.set(getString(R.string.codi_consulta_validacion), consultaValidacionUrl.url)
                    }

                codiUrls.firstOrNull { item -> item.clave == getString(R.string.codi_registra_app_omision)}
                    ?.let { registroOmisionUrl ->
                        Prefs.set(getString(R.string.codi_registra_app_omision), registroOmisionUrl.url)
                    }

                val appCodiModules = createAppCodiModules()

                loadKoinModules(appCodiModules)


                MainApplication.phone = viewModel.phone.value!!
                MainApplication.pass = viewModel.password.value!!
            }

            it.successResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()

                when (codeResponseData.codigo) {
                    0 -> {
                        launchDashboardActivity()
                    }

                    -4 -> {
                        val revalidateAccount = ASPMaterialDialogCustom.newInstance(
                            "Información",
                            codeResponseData.mensaje,
                            "cerrar",
                            ASPMaterialDialogCustom.DialogIconType.INFO.ordinal,
                            true,
                            UNLOCK_CARD_OPTION_TYPE
                        )

                        revalidateAccount.setASPMaterialDialogCustomListener(object :
                            ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {
                            override fun onClickAcceptButton(optionType: Int) {
                                revalidateAccount.dismiss()
                                launchValidatePhone()
                            }

                            override fun onClickClose() {
                                revalidateAccount.dismiss()
                            }
                        })

                        revalidateAccount.show(childFragmentManager, ASPMaterialDialogCustom.TAG)


                    }

                    else -> {
                        showCustomDialogInfo(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                dialog.dismiss()
                showCustomDialogError(
                    "Información",
                    errorMessage,
                )
            }
        }
    }

    private fun launchValidatePhone() {
        safeNavigate(R.id.action_registerInfo_to_revalidateAccountFragment)
    }

    private fun validateBiometrics() {
        biometricPrompt.authenticate(promptInfo)
    }

    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                binding.buttonValidation.isEnabled = true

            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                binding.buttonValidation.isEnabled = false

            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                binding.buttonValidation.isEnabled = false

            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }

                binding.buttonValidation.isEnabled = false

                startActivityForResult(enrollIntent, 100)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {

            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {

            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {

            }
        }
    }

    override fun onClickBackPressed() {
        findNavController().popBackStack()
    }

    override fun onClickWhatsappIcon() {
        requireActivity().showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )
    }
}