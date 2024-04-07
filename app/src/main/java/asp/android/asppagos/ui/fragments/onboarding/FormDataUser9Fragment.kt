package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.aspandroidmaterial.ui.adapters.CountryCodeSpinnerAdapter
import asp.android.aspandroidmaterial.ui.models.CountryDisplayItem
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentRegisterLoginBinding
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser9Fragment : BaseFragment() {

    private var _binding: FragmentRegisterLoginBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 10)

            it.textInformation.visibility = View.VISIBLE

            it.textInformation.text =
                getString(R.string.onboarding_form_title_hint_phone_activation_button_message)
            it.editTextPhone.hint = getString(R.string.onboarding_form_title_hint_phone)
            it.textViewRegisterLoginTitle.text =
                getString(R.string.onboarding_form_title_toolbar_text_phone_activation)

            it.editTextPhone.inputType = InputType.TYPE_CLASS_PHONE
            it.editTextPhone.requestFocus()
            it.editTextPhone.showKeyboard()

            it.editTextPhone.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.continueButton.isEnabled =
                        p0!!.isNotEmpty()
                                && android.util.Patterns.PHONE.matcher(p0).matches()
                                && p0.length == 10
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

            it.ASPTMaterialToolbarRegisterLogin.setASPMaterialToolbarsListeners(this)

            it.continueButton.setOnClickListener {
                dialog.show(
                    requireActivity().supportFragmentManager,
                    TAG
                )
                viewModel.setPhone(binding.editTextPhone.text.toString())
                viewModel.verifiedPhone()
            }

            it.spinnerCountry.adapter = CountryCodeSpinnerAdapter(
                requireContext(),
                listOf(
                    CountryDisplayItem(
                        "+52",
                        "Mexico",
                        asp.android.aspandroidmaterial.R.drawable.flag_icon
                    )
                )
            )

            it.spinnerCountry.setSelection(1, true)
            it.spinnerCountry.isEnabled = false
            it.spinnerCountry.isClickable = false
        }

        initViewModel()
    }

    private fun initViewModel() {

        viewModel.let {
            it.successVerifiedResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()

                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal, 5 -> {
                        it.sendCode()
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
                        safeNavigate(R.id.action_formDataUser9Fragment_to_formDataUser10Fragment)
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