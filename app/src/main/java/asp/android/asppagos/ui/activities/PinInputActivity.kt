package asp.android.asppagos.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialInfoDialog
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.databinding.FragmentCodeSecurityBinding
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.PinInputCodeSecurityViewModel
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN_REGISTER
import asp.android.asppagos.utils.PROPERTY_PIN_VALUE
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.showKeyboard
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

class PinInputActivity : AppCompatActivity(), ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners, ASPMaterialInfoDialog.Companion.DialogDismissListener {

    private lateinit var binding: FragmentCodeSecurityBinding

    private val viewModel: PinInputCodeSecurityViewModel by inject()

    private lateinit var dialogInfo: ASPMaterialInfoDialog
    private val isBiometricActive = Prefs[PROPERTY_FINGER_TOKEN_REGISTER, false]
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var showBiometric = false

    companion object {
        const val SHOW_BIOMETRIC = "showBiometric"
        const val IS_BIOMETRIC_SUCCESS = "isBiometric"
        const val BIOMETRIC_MSG = "biometricMessage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = FragmentCodeSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showBiometric = intent.getBooleanExtra(SHOW_BIOMETRIC, false)

        createBiometricPrompt()

        setupObservable()

        binding.let {
            it.bContinueSendMoney.setOnClickListener {
                val pinValue = binding.createPinValidator.text.toString()
                val intent = Intent()
                intent.putExtra(PROPERTY_PIN_VALUE, pinValue)
                setResult(RESULT_OK, intent)
                finish()
            }

            it.createPinValidator.doOnTextChanged { text, _, _, _ ->
                binding.bContinueSendMoney.isEnabled = text?.length == 4
            }

            it.toolbar.setASPMaterialToolbarsListeners(this)

            it.createPinValidator.requestFocus()
            it.createPinValidator.showKeyboard()
        }

        validateBiometric()
    }

    private fun setupObservable() {
        viewModel.uiState.observe(this) {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: UIStates<Boolean>) {
        when (state) {
            is UIStates.Success -> handleSuccess()
            is UIStates.Error -> handleError(state)
            else -> {}
        }
    }

    private fun handleSuccess() {
        val intent = Intent()
        intent.putExtra(IS_BIOMETRIC_SUCCESS, true)
        setResult(RESULT_OK, intent)
        viewModel.resetUiState()
        finish()
    }

    private fun handleError(error: UIStates.Error) {
        val intent = Intent()
        intent.putExtra(IS_BIOMETRIC_SUCCESS, false)
        intent.putExtra(BIOMETRIC_MSG, error.message)
        setResult(RESULT_OK, intent)
        viewModel.resetUiState()
        finish()
    }

    private fun validateBiometric() {
        if (isBiometricActive && showBiometric) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun createBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != 10) {
                        Toast.makeText(
                            this@PinInputActivity,
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
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@PinInputActivity, "Authentication failed",
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
        viewModel.checkPinUser( pinRequestTypeTransaction = PinRequestTypeTransaction.PIN_ASIGNATION, isBiometric = true)
    }

    override fun onClickWhatsappIcon() {
        dialogInfo = ASPMaterialInfoDialog.newInstance(this)
        dialogInfo.show(supportFragmentManager, ASPMaterialInfoDialog.TAG)
    }

    override fun onClickBackButton() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        viewModel.resetUiState()
        finish()
    }

    override fun onDialogDismissed() {
        dialogInfo.dismiss()
    }
}