package asp.android.asppagos.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentCodeSecurityBinding
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN_REGISTER
import asp.android.asppagos.utils.PROPERTY_PASSWORD_REGISTER
import asp.android.asppagos.utils.Prefs
import java.util.concurrent.Executor

abstract class BaseCodeSecurityFragment: BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners {

    private val isBiometricActive = Prefs[PROPERTY_FINGER_TOKEN_REGISTER, false]
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    /**
     * Variable to indicate if biometric prompt should be shown or not.
     * Used to avoid biometric auth from being requested when navigating back to the main menu
     * using the system's back button, after a successful operation.
     */
    abstract var shouldAuthenticate: Boolean

    private val binding : FragmentCodeSecurityBinding by lazy {
        FragmentCodeSecurityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBiometricPrompt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPersonalCode()
        setupOnClickListener()
        if (shouldAuthenticate) {
            validateBiometric()
        }
    }

    fun setupToolbar(idTitle: Int) {
        binding.toolbar.setTitle(getString(idTitle))
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }
    
    private fun setupPersonalCode() {
        binding.createPinValidator.doOnTextChanged { text, _, _, _ ->
            binding.bContinueSendMoney.isEnabled = text?.length == 4
        }
    }

    private fun setupOnClickListener() {
        binding.apply {
            bContinueSendMoney.setOnClickListener {
                continueToFlow(binding.createPinValidator.text.toString())
            }
        }
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

    abstract fun continueToFlow(pinSecurity: String)

    abstract fun biometricSuccess()
}