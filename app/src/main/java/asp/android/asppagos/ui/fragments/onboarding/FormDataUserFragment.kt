package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.AppSignatureHashHelper
import asp.android.asppagos.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUserFragment : BaseFragment() {

    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

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

        AppSignatureHashHelper.TAG

        binding.let {

            it.indicadorDots.setIndicators(12, 0)

            it.inputForm.requestFocus()
            it.inputForm.showKeyboard()

            it.inputForm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled = (p0!!.isNotEmpty())
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.textViewTitleViewHead.text =
                getString(R.string.onboarding_form_title_toolbar_text_address)
            it.recyclerViewFormDataUser.visibility = View.GONE

            it.recyclerViewFormDataUser.adapter = DataConfirmationAdapter(
                emptyList()
            ) { dataToEdit ->

            }

            it.inputForm.hint = getString(R.string.onboarding_form_title_hin_street)

            it.buttonContinueForm.setOnClickListener {
                viewModel.setStreet(binding.inputForm.editableText.toString())
                safeNavigate(R.id.action_formDataUserFragment_to_formDataUser2Fragment)
            }
        }
    }
}