package asp.android.asppagos.ui.fragments.configurations

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.layouts.ASPMaterialDialogCustom
import asp.android.aspandroidmaterial.ui.toolbars.ASPMaterialToolbarMainDashboard
import asp.android.asppagos.R
import asp.android.asppagos.data.events.SMSEvent
import asp.android.asppagos.databinding.FragmentUpdatePersonalCodeSmsBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.configurations.UpdatePersonalCodeSmsViewModel
import asp.android.asppagos.utils.RxBus
import asp.android.asppagos.utils.append
import asp.android.asppagos.utils.appendWithFont
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showCustomDialogSuccess
import asp.android.asppagos.utils.toEditable
import org.koin.android.ext.android.inject

class UpdatePersonalCodeSmsFragment : BaseFragment(),
    ASPMaterialToolbarMainDashboard.ASPMaterialToolbarMainDashboardListeners,
    ASPMaterialDialogCustom.ASPMaterialDialogCustomListener {

    override var TAG: String = this.javaClass.name

    private val binding: FragmentUpdatePersonalCodeSmsBinding by lazy {
        FragmentUpdatePersonalCodeSmsBinding.inflate(layoutInflater)
    }

    private val viewModel: UpdatePersonalCodeSmsViewModel by inject()

    private lateinit var timer: CountDownTimer
    private var isResend: Boolean = false
    private var isCounting: Boolean = false

    private val showDialogSuccess = ASPMaterialDialogCustom.newInstance(
        title = "Código actualizado",
        subTitle = "Código de seguridad personal fue cambiado exitosamente.",
        textOption1 = "cerrar",
        dialogType = ASPMaterialDialogCustom.DialogIconType.SUCCESS.id,
        visibleAcceptButton = false
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(UPDATE_PIN)?.let { pinCode ->
                viewModel.setupPinCode(pinCode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTimer()
        setupListenerDialog()
        setupOnClickListener()
        setupWritingListener()
        setupObservable()
        setupAutomaticSMS()
    }

    private fun setupListenerDialog() {
        showDialogSuccess.setASPMaterialDialogCustomListener(this)
    }

    private fun setupToolbar() {
        binding.toolbar.setTitle("Actualiza número de\n" + "seguridad personal")
        binding.toolbar.setASPMaterialToolbarsListeners(this)
    }

    private fun setupWritingListener() {
        binding.validatorPinSms.addTextChangedListener {
            binding.btnContinue.isEnabled = it.toString().length == 6
        }
    }

    private fun setupOnClickListener() {
        binding.btnContinue.setOnClickListener {
            viewModel.validateSMSCode(binding.validatorPinSms.text.toString())
        }
    }

    override fun onClickAcceptButton(optionType: Int) {
        viewModel.restartState()
        moveToDashboard()
    }

    override fun onClickClose() {
        moveToProfileSettings()
    }

    override fun onClickBackButton() {
        onClickBackPressed()
    }

    private fun setupTimer() {
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // long minutes = (milliseconds / 1000) / 60;
                val minutes = millisUntilFinished / 1000 / 60

                // long seconds = (milliseconds / 1000);
                val seconds = millisUntilFinished / 1000 % 60

                binding.let {

                    it.textViewResendCode.text = ""

                    it.textViewResendCode.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textViewResendCode.append(
                        "$minutes:$seconds\n\n", R.color.text_3
                    )

                    it.textViewResendCode.append(
                        "No recibí el código, ", R.color.text_1
                    )

                    it.textViewResendCode.appendWithFont(
                        "reenviar", R.color.text_disable
                    )
                }
            }

            override fun onFinish() {
                binding.let {
                    it.textViewResendCode.text = ""

                    it.textViewResendCode.append(
                        "El proceso puede tardar ", R.color.text_1
                    )
                    it.textViewResendCode.append(
                        "\n\n", R.color.text_3
                    )

                    it.textViewResendCode.append(
                        "No recibí el código, ", R.color.text_1
                    )

                    it.textViewResendCode.appendWithFont(
                        "reenviar", R.color.text_1
                    )
                }

                isCounting = false
            }
        }
        binding.textViewResendCode.setOnClickListener {
            if (!isCounting) {
                timer.start()

                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )
                isResend = true
            }
        }
        viewModel.sendSms()
        timer.start()
    }

    private fun setupObservable() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: UIStates<Any>) {
        when (state) {
            is UIStates.Init -> {}
            is UIStates.Success -> handleSuccess(state)
            is UIStates.Loading -> dialog.show(childFragmentManager, TAG)
            is UIStates.Error -> handleError(state)
        }
    }

    private fun handleError(error: UIStates.Error) {
        dialog.dismiss()
        showCustomDialogError(message1 = error.message, message2 = error.message)
    }

    private fun handleSuccess(success: UIStates.Success<Any>) {
        dialog.dismiss()
        showDialogSuccess.show(childFragmentManager, TAG)
    }

    private fun moveToDashboard() {
        safeNavigate(
            R.id.dashboardMainFragment, args = null, navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.dashboardMainFragment, inclusive = true)
            }.build()
        )
    }

    private fun moveToProfileSettings() {
        Log.d("JHMM", "moveToProfileSettings")
        safeNavigate(
            R.id.configurationOptionsFragment, args = null, navOptions = NavOptions.Builder().apply {
                setLaunchSingleTop(true)
                setPopUpTo(R.id.configurationOptionsFragment, inclusive = true)
            }.build()
        )
    }
    private fun setupAutomaticSMS() {
        RxBus.listen(SMSEvent::class.java).subscribe {
            binding.validatorPinSms.text =
                it.code.replace("\\D".toRegex(), "").toEditable()
        }
    }

    companion object {
        const val UPDATE_PIN = "update_pin"
    }

}