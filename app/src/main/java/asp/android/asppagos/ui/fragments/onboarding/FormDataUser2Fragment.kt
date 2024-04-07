package asp.android.asppagos.ui.fragments.onboarding

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
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
import asp.android.asppagos.utils.showKeyboard
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser2Fragment : BaseFragment() {

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
        if (requestCode == DATA_MODIFIED_CODE && resultCode == RESULT_OK) {
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
                    resources.getStringArray(R.array.onboarding_user_form_titles)[0].toString(),
                    viewModel.street.value!!,
                    EditorType.Address
                )
            )

        binding.let {

            it.indicadorDots.setIndicators(dotsCount, 1)

            it.textViewTitleViewHead.text = getString(R.string.onboarding_form_title_num_ext)

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            it.recyclerViewFormDataUser.visibility = View.VISIBLE

            mainAdapter = DataConfirmationAdapter(
                userDataList
            ) { itemToEdit ->
                updateItem(itemToEdit)
            }

            it.recyclerViewFormDataUser.adapter = mainAdapter

            it.recyclerViewFormDataUser.layoutManager = LinearLayoutManager(requireContext())

            it.inputForm.visibility = View.GONE
            it.InputForm2.visibility = View.VISIBLE
            it.InputForm3.visibility = View.VISIBLE
            it.InputForm2.hint = getString(R.string.onboarding_form_title_hin_num_ext)
            it.InputForm3.hint = getString(R.string.onboarding_form_title_hin_num_int)

            it.InputForm2.requestFocus()
            it.InputForm2.showKeyboard()

            it.InputForm2.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled =
                        p0!!.isNotEmpty()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            it.InputForm3.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    it.buttonContinueForm.isEnabled =
                        it.InputForm2.text.toString().isNotEmpty()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })

            it.buttonContinueForm.setOnClickListener {

                viewModel.setNumInt(binding.InputForm3.text.toString())
                viewModel.setNumExt(binding.InputForm2.text.toString())

                safeNavigate(R.id.action_formDataUser2Fragment_to_formDataUser3Fragment)
            }
        }
    }
}