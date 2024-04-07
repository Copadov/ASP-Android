package asp.android.asppagos.ui.fragments.onboarding

import android.app.Activity
import android.content.Intent
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
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.MODIFY_DATA
import asp.android.asppagos.utils.showCustomDialogError
import asp.android.asppagos.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser3Fragment : BaseFragment() {

    private lateinit var mainAdapter: DataConfirmationAdapter
    private var userDataList: MutableList<DataItem> = mutableListOf()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA_MODIFIED_CODE && resultCode == Activity.RESULT_OK) {
            val modifiedDataItem = data?.getParcelableExtra<DataItem>(MODIFY_DATA)
            if (modifiedDataItem != null) {

                val itemToUpdate = userDataList.find { it.type == modifiedDataItem.type }

                val position = userDataList.indexOf(itemToUpdate)

                if (position != -1) {
                    userDataList[position] = modifiedDataItem
                    // Notifica al adaptador sobre el cambio en el item modificado
                    mainAdapter.notifyItemChanged(position)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDataList =
            mutableListOf(
                DataItem(
                    getString(R.string.onboarding_form_title_street),
                    viewModel.street.value!!,
                    EditorType.Address
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_num_ext),
                    viewModel.numExt.value!!,
                    EditorType.NumExt
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_num_int),
                    viewModel.numInt.value!!,
                    EditorType.NumInt
                )
            )

        binding.let { it ->

            it.indicadorDots.setIndicators(dotsCount, 2)

            it.textViewTitleViewHead.text = getString(R.string.onboarding_form_title_cp)
            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.VISIBLE

            mainAdapter = DataConfirmationAdapter(
                userDataList
            ) { itemToEdit ->
                updateItem(itemToEdit)
            }

            it.recyclerViewFormDataUser.adapter = mainAdapter

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.VISIBLE
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE
            it.inputForm.hint = getString(R.string.onboarding_form_title_hint_cp)

            it.inputForm.requestFocus()
            it.inputForm.showKeyboard()

            it.inputForm.filters = arrayOf(InputFilter.LengthFilter(5))
            it.inputForm.inputType = InputType.TYPE_CLASS_NUMBER

            it.inputForm.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled =
                        (p0!!.isNotEmpty()
                                && p0.length == 5)
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            it.buttonContinueForm.setOnClickListener {
                dialog.show(requireActivity().supportFragmentManager, TAG)
                viewModel.setCpCode(binding.inputForm.text.toString())
                viewModel.validateCP()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.let {
            it.cpValidateResponse.observe(viewLifecycleOwner) { responseData ->
                if (responseData.isNotEmpty()) {
                    dialog.dismiss()
                    safeNavigate(R.id.action_formDataUser3Fragment_to_formDataUser4Fragment)
                } else {
                    dialog.dismiss()
                    showCustomDialogError(
                        "Información",
                        "El código postal no ha sido encontrado.",
                    )
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