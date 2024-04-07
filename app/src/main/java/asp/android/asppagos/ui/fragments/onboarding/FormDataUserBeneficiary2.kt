package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormBeneficiaryBinding
import asp.android.asppagos.ui.adapters.CitySpinnerArrayAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.NoSpecialCharacterFilter
import asp.android.asppagos.utils.showKeyboard

class FormDataUserBeneficiary2 : BaseFragment() {

    private var _binding: FragmentFormBeneficiaryBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModels()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormBeneficiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateConfirmButtonState()

        binding.let {
            val param = it.buttonContinueForm.layoutParams as ViewGroup.MarginLayoutParams
            param.marginEnd = 15
            it.buttonContinueForm.layoutParams = param

            it.indicadorDots.setIndicators(dotsCount, 9)

            it.toolbarContent.contentToolbarTitle.text =
                getString(R.string.beneficiary_text_2_title)
            it.toolbarContent.contentToolbarSubtitle.text =
                getString(R.string.beneficiary_text_2_subtitle)

            it.ASPMaterialToolbarFormDataBeneficiary.setASPMaterialToolbarsListeners(this)

            it.inputFormName.hint = getString(R.string.beneficiary_name_hint_text)
            it.inputFormLastName.hint = getString(R.string.beneficiary_lastname_hint_text)
            it.inputFormSurname.hint = getString(R.string.beneficiary_surname_text)

            it.inputFormName.filters = arrayOf(NoSpecialCharacterFilter())
            it.inputFormLastName.filters = arrayOf(NoSpecialCharacterFilter())
            it.inputFormSurname.filters = arrayOf(NoSpecialCharacterFilter())

            it.inputFormName.requestFocus()
            it.inputFormName.showKeyboard()

            it.spinnerCity.tooltipText = getString(R.string.beneficiary_simil_hint_text)
            it.spinnerCity.adapter =
                CitySpinnerArrayAdapter(
                    requireContext(),
                    R.layout.spinner_city_layout_item,
                    listOf(
                        CitySpinnerArrayAdapter.CityModel(1, "Familiar"),
                        CitySpinnerArrayAdapter.CityModel(2, "Amigo"),
                        CitySpinnerArrayAdapter.CityModel(11, "Padre"),
                        CitySpinnerArrayAdapter.CityModel(12, "Madre"),
                        CitySpinnerArrayAdapter.CityModel(13, "Hijo(a)"),
                        CitySpinnerArrayAdapter.CityModel(25, "Conyuge"),
                    )
                )

            it.inputFormName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.setNameBeneficiary(s.toString())
                    updateConfirmButtonState()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            it.inputFormLastName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.setLastNameBeneficiary(s.toString())
                    updateConfirmButtonState()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            it.inputFormSurname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.setSurNameBeneficiary(s.toString())
                    updateConfirmButtonState()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            it.buttonContinueForm.setOnClickListener {
                viewModel
                    .setSimilBeneficiary(
                        (binding.spinnerCity.selectedItem
                                as CitySpinnerArrayAdapter.CityModel)
                            .id
                    )

                safeNavigate(R.id.action_formDataUserBeneficiary2_to_formDataUser11Fragment)
            }
        }

        initViewModel()
    }

    private fun updateConfirmButtonState() {
        val isFieldsNotEmpty = areFieldsNotEmpty()
        binding.buttonContinueForm.isEnabled = isFieldsNotEmpty
    }

    private fun areFieldsNotEmpty(): Boolean {
        val name = binding.inputFormName.text.toString().trim()
        val lastName = binding.inputFormLastName.text.toString().trim()
        val surname = binding.inputFormSurname.text.toString().trim()

        return name.isNotEmpty()
                && lastName.isNotEmpty()
                && surname.isNotEmpty()
    }


    private fun initViewModel() {

    }
}