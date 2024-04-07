package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.ServerErrorCodes
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showKeyboard

class FormDataUser7Fragment : BaseFragment() {

    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDataUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 6)

            it.textViewTitleViewHead.text =
                getString(R.string.onboarding_form_title_toolbar_text_email)

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.VISIBLE

            it.recyclerViewFormDataUser.adapter = DataConfirmationAdapter(
                emptyList()
            ) {}

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.VISIBLE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE
            it.inputForm.hint = getString(R.string.onboarding_form_title_hint_email)
            it.textDescriptive.visibility = View.VISIBLE
            it.textDescriptive.text = getString(R.string.onboarding_form_title_hint_email_message)
            it.inputForm.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS

            it.inputForm.requestFocus()
            it.inputForm.showKeyboard()

            it.inputForm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled =
                        p0!!.isNotEmpty()
                                &&
                                android.util.Patterns.EMAIL_ADDRESS.matcher(p0).matches()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.buttonContinueForm.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, TAG)
                viewModel.setEmail(binding.inputForm.text.toString())
                viewModel.validateEmail("1")
            }
        }

        initViewModel()
    }

    private fun initViewModel() {

        viewModel.let { it ->
            it.successResponse.observe(viewLifecycleOwner) { codeResponseData ->
                dialog.dismiss()
                when (codeResponseData.codigo) {
                    ServerErrorCodes.SUCCESS.ordinal -> {
                        safeNavigate(R.id.action_formDataUser7Fragment_to_formDataUser8Fragment)
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