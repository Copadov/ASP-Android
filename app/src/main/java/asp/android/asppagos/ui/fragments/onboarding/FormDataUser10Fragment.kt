package asp.android.asppagos.ui.fragments.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.data.events.SMSEvent
import asp.android.asppagos.databinding.FragmentCodePinValidationBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.*
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser10Fragment : BaseFragment() {

    private var isCounting: Boolean = false
    private lateinit var timer: CountDownTimer
    private var isResend: Boolean = false
    private var _binding: FragmentCodePinValidationBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCodePinValidationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override var TAG: String = this.javaClass.name

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        RxBus.listen(SMSEvent::class.java).subscribe {
            binding.codePinValidator.text =
                it.code.replace("\\D".toRegex(), "").toEditable()
        }

        binding.let {

            timer.start()
            isCounting = true

            it.indicadorDots.setIndicators(12, 9)

            it.codePinValidator.requestFocus()
            it.codePinValidator.showKeyboard()

            it.toolbarcontent.contentToolbarTitle.text =
                getString(R.string.onboarding_form_title_toolbar_text_pin_validation)

            it.dataConfirmation.imageViewEditData.visibility = View.INVISIBLE
            it.dataConfirmation.TextViewText.text = viewModel.phone.value!!
            it.dataConfirmation.TextviewTitle.text =
                getString(R.string.onboarding_form_title_phone)

            it.toolbarcontent.ASPTMaterialToolbarContent.setASPMaterialToolbarsListeners(this)

            it.codePinValidator.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.continueButton.isEnabled =
                        p0!!.isNotEmpty()
                                && p0.length == 6
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.continueButton.setOnClickListener {
                requireActivity().hideKeyboard()
                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )
                viewModel.setToken(binding.codePinValidator.text.toString())
                viewModel.validatePhone()
            }

            it.textViewResendCode.text = ""

            it.textViewResendCode.append(
                "El proceso puede tardar ", R.color.text_1
            )
            it.textViewResendCode.append(
                "1:00\n\n", R.color.text_3
            )

            it.textViewResendCode.append(
                "No recibí el código, ", R.color.text_1
            )

            it.textViewResendCode.appendWithFont(
                "reenviar", R.color.text_disable
            )

            it.textViewResendCode.setOnClickListener {
                if (!isCounting) {
                    timer.start()

                    dialog.show(
                        requireActivity().supportFragmentManager,
                        TAG
                    )

                    viewModel.sendCode()
                    isResend = true
                }
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.successResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()

                when (codeResponseData.codigo) {
                    0 -> {
                        safeNavigate(
                                R.id.action_formDataUser10Fragment_to_formDataUserBeneficiaryInfo
                            )

                        Prefs.set(BENEFICIARY_ACTIVATED, true)
                    }

                    1, 4, 8, 9, 10, 11, 13 -> {
                        showCustomDialogError(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }

                    12 -> {
                        safeNavigate(
                                R.id.action_formDataUser10Fragment_to_formDataUser11Fragment
                            )

                        Prefs.set(BENEFICIARY_ACTIVATED, false)
                    }

                    else -> {
                        showCustomDialogError(
                            "Información",
                            codeResponseData.mensaje,
                        )
                    }
                }
            }

            it.successSendCodeResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        showCustomDialogSuccess(
                            "Información",
                            codeResponseData.mensaje
                        )
                    }

                    else -> {
                        showCustomDialogError(
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
}