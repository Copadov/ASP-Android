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
import asp.android.asppagos.data.models.CPResponseData
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.databinding.FragmentFormDataUserBinding
import asp.android.asppagos.ui.adapters.CitySpinnerArrayAdapter
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.MODIFY_DATA
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FormDataUser4Fragment : BaseFragment() {

    private lateinit var mainAdapter: DataConfirmationAdapter
    private var userDataList: MutableList<DataItem> = mutableListOf()
    private var _binding: FragmentFormDataUserBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    /**
     * List to show on Spinner
     */
    private var citiesList = mutableListOf <CitySpinnerArrayAdapter.CityModel>()

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
                )
            )

        binding.let {

            it.indicadorDots.setIndicators(dotsCount, 3)

            it.textViewTitleViewHead.text = getString(R.string.onboarding_form_title_colony)

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
            it.inputForm.hint = getString(R.string.onboarding_form_title_hint_colony)

            it.spinnerCity.visibility = View.VISIBLE
            it.spinnerCity.tooltipText = getString(R.string.onboarding_form_title_hint_colony)

            citiesList.clear()
            citiesList.addAll(
                if (viewModel.cpValidateResponse.value!!.isEmpty())
                    listOf(
                        CitySpinnerArrayAdapter.CityModel(1, "Colonia 1"),
                        CitySpinnerArrayAdapter.CityModel(2, "Colonia 2"),
                        CitySpinnerArrayAdapter.CityModel(3, "Colonia 3"),
                    )
                else
                    viewModel.cpValidateResponse.value!!.map { cpResponse ->
                        CitySpinnerArrayAdapter.CityModel(0, cpResponse.colonia)
                    }.toList()
            )

            it.spinnerCity.adapter =
                CitySpinnerArrayAdapter(
                    requireContext(),
                    R.layout.spinner_city_layout_item,
                    citiesList
                )

            it.buttonContinueForm.isEnabled = true

            it.buttonContinueForm.setOnClickListener {

                viewModel
                    .setColony(
                        (binding.spinnerCity.selectedItem
                                as CitySpinnerArrayAdapter.CityModel)
                            .nameCity
                    )

                obtenerElementoPorColonia(
                    viewModel.cpValidateResponse.value!!,
                    viewModel.colony.value!!
                )?.let { cpSelected ->
                    viewModel.setCPResponse(
                        cpSelected
                    )
                }

                safeNavigate(R.id.action_formDataUser4Fragment_to_formDataUser5Fragment)
            }
        }

        initViewModel()
    }

    fun obtenerElementoPorColonia(
        lista: List<CPResponseData>,
        coloniaBuscada: String
    ): CPResponseData? {
        return lista.find { it.colonia == coloniaBuscada }
    }

    /**
     * Method to start viewmodel observers
     */
    private fun initViewModel() {
        viewModel.cpValidateResponse.observe(viewLifecycleOwner) { listResponse ->
            binding.spinnerCity.adapter?.let { adapter ->
                // Actualiza lista de colonias cuando cambia el CP
                citiesList.clear()
                citiesList.addAll(
                    if (listResponse.isEmpty())
                        listOf(
                            CitySpinnerArrayAdapter.CityModel(1, "Colonia 1"),
                            CitySpinnerArrayAdapter.CityModel(2, "Colonia 2"),
                            CitySpinnerArrayAdapter.CityModel(3, "Colonia 3"),
                        )
                    else
                        listResponse.map { cpResponse ->
                            CitySpinnerArrayAdapter.CityModel(0, cpResponse.colonia)
                        }.toList()
                )

                (adapter as CitySpinnerArrayAdapter).notifyDataSetChanged()
            }

        }
    }
}