package asp.android.asppagos.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import asp.android.aspandroidmaterial.ui.toolbars.ASPTMaterialToolbar
import asp.android.asppagos.R
import asp.android.asppagos.data.models.DataItem
import asp.android.asppagos.databinding.ActivityUserDataEditBinding
import asp.android.asppagos.ui.adapters.CitySpinnerArrayAdapter
import asp.android.asppagos.ui.fragments.BaseFragment
import asp.android.asppagos.utils.*
import java.util.Calendar

class UserDataEditActivity : AppCompatActivity(), ASPTMaterialToolbar.ASPMaterialToolbarsListeners {

    private lateinit var dataItem: DataItem
    private lateinit var binding: ActivityUserDataEditBinding
    private lateinit var editName: String
    private lateinit var inputToEdit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityUserDataEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataItem = intent.getParcelableExtra(MODIFY_DATA)!!

        editName = dataItem!!.name
        inputToEdit = dataItem.value

        binding.let {

            it.indicadorDots.setIndicators(12, 0)

            it.textViewTitleViewHead.text =
                getString(
                    R.string.onboarding_form_title_edit_data_user_toolbar_text,
                    editName.lowercase()
                )

            it.ASPMaterialToolbarFormData.setASPMaterialToolbarsListeners(this)

            when (dataItem.type) {
                BaseFragment.EditorType.BirthDate -> {
                    it.spinnerCity.visibility = View.GONE
                    it.inputForm.visibility = View.GONE
                    it.dateInput.visibility = View.VISIBLE
                    it.dateInput.hint = "dd/mm/yyyy"
                    it.dateInput.setRawInputType(InputType.TYPE_CLASS_NUMBER)

                    it.dateInput.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // No es necesario implementar esta función en este caso
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            // No es necesario implementar esta función en este caso
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val inputText = s.toString()
                            val regexPattern = "\\d{2}/\\d{2}/\\d{4}".toRegex()

                            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                            val yearInput = inputText.substringAfterLast('/')

                            val isValidDate =
                                inputText.matches(regexPattern) && (yearInput.toIntOrNull()
                                    ?: 0) <= currentYear

                            it.buttonContinueForm.isEnabled = isValidDate
                        }
                    })
                }

                BaseFragment.EditorType.Genre -> {
                    it.inputForm.visibility = View.GONE
                    it.spinnerCity.visibility = View.VISIBLE
                    it.spinnerCity.adapter = CitySpinnerArrayAdapter(
                        this,
                        R.layout.spinner_city_layout_item,
                        listOf(
                            CitySpinnerArrayAdapter.CityModel(1, "Masculino"),
                            CitySpinnerArrayAdapter.CityModel(1, "Femenino"),
                        )
                    )

                    it.buttonContinueForm.isEnabled = true
                }

                BaseFragment.EditorType.CURP -> {
                    val maxLength = 18
                    val filters = arrayOf<InputFilter>(
                        InputFilter.LengthFilter(maxLength),
                        InputFilter.AllCaps()
                    )
                    it.inputForm.filters = filters
                    it.inputForm.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            val isValidInput = validateInput(s.toString())
                            if (validateInput(s.toString())) {
                                it.inputForm.error = null
                            } else {
                                it.inputForm.error = "Verifica que el CURP sea valido."
                            }
                            it.buttonContinueForm.isEnabled = isValidInput
                        }

                        override fun afterTextChanged(s: Editable?) {}

                        private fun validateInput(input: String): Boolean {
                            // Validar formato de CURP
                            val curpPattern = Regex("[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]{2}")
                            if (curpPattern.matches(input) && input.length == 18) {
                                return true
                            }
                            return false
                        }
                    })
                }

                BaseFragment.EditorType.Name,
                BaseFragment.EditorType.LastName,
                BaseFragment.EditorType.SurName -> {
                    val maxLength = 250
                    val filters = arrayOf(
                        InputFilter.LengthFilter(maxLength),
                        NoSpecialCharacterFilter()
                    )
                    it.inputForm.filters = filters

                    it.inputForm.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val isInputEmpty = s?.isEmpty() ?: true
                            it.buttonContinueForm.isEnabled = !isInputEmpty
                        }
                    })
                }

                else -> {
                }
            }

            it.inputForm.text =
                inputToEdit.toEditable()

            it.buttonContinueForm.isEnabled = inputToEdit.toEditable().isNotEmpty()
            it.inputForm.requestFocus()
            it.inputForm.showKeyboard()
            it.buttonContinueForm.setOnClickListener {
                setResultToActivity()
            }
        }
    }

    private fun validateDateFormat(dateString: String): Boolean {
        val pattern = Regex("\\d{2}/\\d{2}/\\d{4}")
        return pattern.matches(dateString)
    }

    private fun setResultToActivity() {
        intent = Intent()
        when (editName) {
            getString(R.string.onboarding_form_title_birthdate_list_value) -> {
                dataItem.value = binding.dateInput.text.toString()
                //intent.putExtra(DATA_MODIFIED_EXTRA, binding.dateInput.text.toString())
            }

            getString(R.string.onboarding_form_title_genre_list_value) -> {
                dataItem.value = (binding.spinnerCity.selectedItem
                        as
                        CitySpinnerArrayAdapter.CityModel).nameCity
                /*intent.putExtra(
                    DATA_MODIFIED_EXTRA,
                    (binding.spinnerCity.selectedItem
                            as
                            CitySpinnerArrayAdapter.CityModel).nameCity
                )*/
            }

            else -> {
                dataItem.value = binding.inputForm.text.toString()
                //intent.putExtra(DATA_MODIFIED_EXTRA, binding.inputForm.text.toString())
            }
        }

        intent.putExtra(MODIFY_DATA, dataItem)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onClickBackPressed() {
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onClickWhatsappIcon() {
        this.showSingleButtonDialog(
            "Información",
            "call_center@aspintegraopciones.com\n664 204 1866",
            "Aceptar"
        )
    }
}