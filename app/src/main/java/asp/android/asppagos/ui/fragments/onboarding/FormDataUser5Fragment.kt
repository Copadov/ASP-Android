package asp.android.asppagos.ui.fragments.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.CitySpinnerArrayAdapter
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.MODIFY_DATA
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser5Fragment : BaseFragment() {

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

                    // Si es tipo CP, actualiza lista de colonias
                    if (modifiedDataItem.type == EditorType.Cp) {
                        viewModel.setCpCode(modifiedDataItem.value)
                        viewModel.validateCP()
                    }
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
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_cp),
                    viewModel.cpCode.value!!,
                    EditorType.Cp
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_colony),
                    viewModel.colony.value!!,
                    EditorType.Colony
                )
            )

        binding.let {
            it.indicadorDots.setIndicators(dotsCount, 4)

            it.textViewTitleViewHead.text = getString(R.string.onboarding_form_title_city)

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
            it.InputForm2.visibility = View.GONE
            it.InputForm3.visibility = View.GONE

            it.spinnerCity.visibility = View.VISIBLE
            it.spinnerCity.tooltipText = getString(R.string.onboarding_form_title_hint_city)
            it.spinnerCity.adapter =
                CitySpinnerArrayAdapter(
                    requireContext(),
                    R.layout.spinner_city_layout_item,
                    if (viewModel.cpValidateResponse.value!!.isEmpty())
                        listOf(
                            CitySpinnerArrayAdapter.CityModel(1, "Ciudad 1"),
                            CitySpinnerArrayAdapter.CityModel(2, "Ciudad 2"),
                            CitySpinnerArrayAdapter.CityModel(3, "Ciudad 3"),
                        )
                    else {
                        listOf(
                            CitySpinnerArrayAdapter.CityModel(
                                0,
                                viewModel.cpSelected.value!!.localidad
                            )

                        )
                    }

                )

            it.buttonContinueForm.isEnabled = true

            it.buttonContinueForm.setOnClickListener {

                viewModel
                    .setCity(
                        (binding.spinnerCity.selectedItem
                                as CitySpinnerArrayAdapter.CityModel).nameCity
                    )

                safeNavigate(R.id.action_formDataUser5Fragment_to_formDataUser6Fragment)
            }
        }

        initViewModel()
    }

    /**
     * Method to start viewmodel observers
     */
    private fun initViewModel() {
        viewModel.cpValidateResponse.observe(viewLifecycleOwner) {
            // Cuando se actualiza el CP, vuelve a la ventana de selección de colonia
            onClickBackPressed()
        }
    }
}