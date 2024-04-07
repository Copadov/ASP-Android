package asp.android.asppagos.ui.fragments.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.RFCDataAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.showKeyboard
import asp.android.asppagos.utils.toEditable
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser6Fragment : BaseFragment() {

    private lateinit var formAdapter: RFCDataAdapter
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

        val listData: List<Pair<String, String>> =
            listOf(
                Pair(
                    getString(R.string.onboarding_form_title_rfc),
                    viewModel.ocrDataResponse.value?.curp!!.take(10) + "***"
                )
            )

        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 5)

            it.textViewTitleViewHead.text =
                getString(R.string.onboarding_form_title_toolbar_text_rfc)

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.VISIBLE

            formAdapter = RFCDataAdapter(
                listData
            ) {}

            it.recyclerViewFormDataUser.adapter = formAdapter

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.GONE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE
            it.textDescriptive.visibility = View.VISIBLE
            it.rfcPin.visibility = View.VISIBLE

            it.textDescriptive.text = getString(R.string.onboarding_form_title_hint_rfc_message)

            it.inputForm.filters = arrayOf(InputFilter.AllCaps(), InputFilter.LengthFilter(13))
            it.rfcPin.filters = arrayOf(InputFilter.AllCaps(), InputFilter.LengthFilter(3))

            viewModel.ocrDataResponse.value?.curp?.let { curp ->
                viewModel.setRFC(curp.take(10))
            }
            it.inputForm.text = viewModel.rfc.value!!.toEditable()

            it.buttonContinueForm.isEnabled = false

            it.rfcPin.requestFocus()
            it.rfcPin.showKeyboard()

            it.rfcPin.inputType = InputType.TYPE_CLASS_TEXT
            it.rfcPin.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val curp = viewModel.ocrDataResponse.value?.curp ?: ""
                    val alphanumericPattern = Regex("[A-Za-z0-9]{3}")
                    val isValidInput = alphanumericPattern.matches(p0.toString())


                    it.buttonContinueForm.isEnabled = isValidInput

                    val textRFC = when (p0?.length) {
                        0 -> curp.take(10) + "***"
                        1 -> curp.take(10) + p0 + "**"
                        2 -> curp.take(10) + p0 + "*"
                        3 -> curp.take(10) + p0
                        else -> ""
                    }

                    updateRFCDataInRecyclerView(textRFC)
                }

                override fun afterTextChanged(p0: Editable?) {}

                private fun updateRFCDataInRecyclerView(textRFC: String) {
                    val dataList =
                        listOf(Pair(getString(R.string.onboarding_form_title_rfc), textRFC))
                    it.recyclerViewFormDataUser.adapter = RFCDataAdapter(dataList) {}
                }
            })

            it.buttonContinueForm.setOnClickListener {
                val rfcCompleted =
                    viewModel.ocrDataResponse.value?.curp!!
                        .take(10) +
                            binding.rfcPin.text.toString()
                viewModel.setRFC(rfcCompleted)
                safeNavigate(R.id.action_formDataUser6Fragment_to_formDataUser7Fragment)
            }
        }
467    }
}