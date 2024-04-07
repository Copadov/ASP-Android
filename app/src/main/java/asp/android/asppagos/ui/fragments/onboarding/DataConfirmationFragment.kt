package asp.android.asppagos.ui.fragments.onboarding

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import asp.android.asppagos.R
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.data.models.OCRResponseData
import asp.android.asppagos.databinding.FragmentDataConfirmationBinding
import asp.android.asppagos.ui.adapters.DataConfirmationAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.ui.viewmodels.OnboardingViewModel
import asp.android.asppagos.utils.DATA_MODIFIED_CODE
import asp.android.asppagos.utils.MODIFY_DATA
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class DataConfirmationFragment : BaseFragment() {

    private lateinit var adapter: DataConfirmationAdapter
    private var _binding: FragmentDataConfirmationBinding? = null
    private val binding get() = _binding!!
    val viewModel: OnboardingViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override var TAG: String = this.javaClass.name

    private fun setDataList(data: OCRResponseData?): List<DataItem> {
        if (data != null) {
            return listOf(
                DataItem(
                    getString(R.string.onboarding_form_title_name_list_value),
                    data.nombres,
                    EditorType.Name
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_lastname_list_value),
                    data.primerApellido,
                    EditorType.LastName
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_surname_list_value),
                    data.segundoApellido,
                    EditorType.SurName
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_birthdate_list_value),
                    data.fechaNacimiento,
                    EditorType.BirthDate
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_genre_list_value),
                    if (data.sexo == "M") "Femenino"
                    else "Masculino",
                    EditorType.Genre
                ),
                DataItem(
                    getString(R.string.onboarding_form_title_curp_list_value),
                    data.curp,
                    EditorType.CURP
                )
            )
        } else {
            return emptyList()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.let {

            it.indicadorDots.setIndicators(2, 0)

            it.ASPTMaterialToolbarDataConfirmation.setASPMaterialToolbarsListeners(this)
            adapter = DataConfirmationAdapter(
                setDataList(viewModel.ocrDataResponse.value)
            ) {
                updateItem(it)
            }

            it.dataConfirmationButton.isEnabled =
                validateDataItems(setDataList(viewModel.ocrDataResponse.value))

            it.recyclerDataConfirmation.adapter = adapter
            it.recyclerDataConfirmation.layoutManager = LinearLayoutManager(requireContext())
            it.recyclerDataConfirmation.setHasFixedSize(true)
            it.dataConfirmationButton.setOnClickListener {
                safeNavigate(R.id.action_dataConfirmationFragment_to_formDataUserFragment)
            }
        }
    }

    fun validateDataItems(dataItems: List<DataItem>): Boolean {
        for (dataItem in dataItems) {
            if (dataItem.value.isEmpty()) {
                return false // El valor está vacío, la validación falla
            }
        }
        return true // Todos los valores están llenos, la validación pasa
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DATA_MODIFIED_CODE && resultCode == RESULT_OK) {
            val modifiedDataItem = data?.getParcelableExtra<DataItem>(MODIFY_DATA)

            modifiedDataItem?.let { item ->
                val ocrDataResponse = viewModel.ocrDataResponse.value
                ocrDataResponse?.apply {
                    when (item.type) {
                        EditorType.Name -> nombres = item.value
                        EditorType.LastName -> primerApellido = item.value
                        EditorType.SurName -> segundoApellido = item.value
                        EditorType.BirthDate -> fechaNacimiento = item.value
                        EditorType.Genre -> sexo = item.value
                        EditorType.CURP -> curp = item.value
                        else -> {}
                    }
                }

                viewModel.ocrDataResponse.value = ocrDataResponse
                binding.dataConfirmationButton.isEnabled =
                    validateDataItems(setDataList(ocrDataResponse))
                adapter.updateListItems(setDataList(ocrDataResponse))
            }
        }
    }
}