package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.showKeyboard

class FormDataUserBeneficiary4 : BaseFragment() {

    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModels()

    override var TAG: String = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormDataUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 6)

            it.textViewTitleViewHead.text = "Captura el e-mail del\n" +
                    "beneficiario"

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.VISIBLE

            it.recyclerViewFormDataUser.adapter = DataConfirmationAdapter(
                emptyList()
            ) {}

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.VISIBLE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE
            it.inputForm.hint = "e-mail del beneficiario*"
            it.textDescriptive.visibility = View.VISIBLE
            it.textDescriptive.text = "Ingresa el correo del beneficiario del contrato."
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
            }
        }

        initViewModel()
    }

    private fun initViewModel() {

    }
}