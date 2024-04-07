package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentCreatePasswordBinding
import asp.android.asppagos.ui.adapters.RestrictionsPasswordAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.showKeyboard
import com.google.android.flexbox.FlexboxLayoutManager
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser11Fragment : BaseFragment() {

    private lateinit var formAdapter: RestrictionsPasswordAdapter
    private lateinit var restrictionsList: MutableList<ValidateRegexPass>

    data class ValidateRegexPass(
        var TextDescription: String = "",
        var IsCorrect: Boolean = false,
        var Validation: ValidatePassType
    )

    private var _binding: FragmentCreatePasswordBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreatePasswordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restrictionsList = mutableListOf(
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_1),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_2),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_3),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_4),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_5),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_6),
                false,
                ValidatePassType.NOTVALIDATED
            ),
            ValidateRegexPass(
                getString(R.string.onboarding_form_title_toolbar_text_pass_criteria_7),
                false,
                ValidatePassType.NOTVALIDATED
            )
        )

        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 12)
            it.toolbarContent.contentToolbarTitle.text =
                getString(R.string.onboarding_form_title_toolbar_text_pass_creation)

            it.toolbarContent.contentToolbarSubtitle.text =
                getString(R.string.onboarding_form_subtitle_toolbar_text_pass_creation)

            formAdapter = RestrictionsPasswordAdapter(
                restrictionsList
            )

            it.recyclerViewRestrictionsPassword.adapter = formAdapter

            it.recyclerViewRestrictionsPassword.layoutManager =
                FlexboxLayoutManager(requireContext())

            it.toolbarContent.ASPTMaterialToolbarContent.setASPMaterialToolbarsListeners(this)

            it.editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            it.editTextPassword.requestFocus()
            it.editTextPassword.showKeyboard()

            it.editTextPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    if (p0!!.isNotEmpty()) {

                        restrictionsList[0].IsCorrect =
                            p0.matches(Regex("^(?=.*[@#\$%^&+=!¡.,\\-_]).*$"))

                        restrictionsList[1].IsCorrect =
                            p0.matches(Regex("^(?=.*[a-z]).+\$"))

                        restrictionsList[2].IsCorrect =
                            p0.matches(Regex("^(?=.*[A-Z]).+\$"))

                        restrictionsList[3].IsCorrect =
                            p0.matches(Regex("^(?=.*[0-9]).+\$"))

                        restrictionsList[4].IsCorrect =
                            p0.matches(Regex("^(?!.*(.)(\\1\\1)).*\$"))

                        restrictionsList[5].IsCorrect =
                            p0.length in 6..20

                        restrictionsList[6].IsCorrect =
                            !p0.contains(
                                //"Carlos",
                                viewModel.ocrDataResponse.value!!.nombres.split(" ")[0],
                                ignoreCase = true
                            )

                        restrictionsList.forEach {
                            it.Validation = ValidatePassType.INCORRECT
                        }

                        formAdapter = RestrictionsPasswordAdapter(
                            restrictionsList
                        )

                        it.recyclerViewRestrictionsPassword.adapter = formAdapter

                        it.continueButton.isEnabled =
                            restrictionsList.filter {
                                it.IsCorrect
                            }.size == 7

                    } else {

                        restrictionsList.forEach {
                            it.Validation = ValidatePassType.NOTVALIDATED
                        }

                        it.recyclerViewRestrictionsPassword.adapter = RestrictionsPasswordAdapter(
                            restrictionsList
                        )
                    }

                    it.recyclerViewRestrictionsPassword.layoutManager =
                        FlexboxLayoutManager(requireContext())
                }
            })

            it.continueButton.setOnClickListener {
                viewModel.setPass(binding.editTextPassword.text.toString())
                safeNavigate(R.id.action_formDataUser11Fragment_to_formDataUser12Fragment)
            }
        }
    }
}

enum class ValidatePassType {
    NOTVALIDATED,
    CORRECT,
    INCORRECT
}
