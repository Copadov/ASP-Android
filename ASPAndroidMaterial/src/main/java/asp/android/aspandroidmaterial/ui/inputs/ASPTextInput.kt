package asp.android.aspandroidmaterial.ui.inputs

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import asp.android.aspandroidmaterial.databinding.AspTextInputBinding
import java.lang.StringBuilder

class ASPTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: AspTextInputBinding =
        AspTextInputBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupInitialField(
        titleField: String,
        hintField: String,
        maxLength: Int = 40,
        maxLines: Int? = null,
        restringOnlyAlphaNumericCharacters: Boolean = false,
        allCaps: Boolean = false,
        prefix: String? = null,
        onChange: (textChange: String) -> Unit
    ) {
        val pattern = "[A-Za-z0-9- ]"
        val filterOnlyAlphanumeric = InputFilter { source, _, _, _, _, _ ->
            if (source != null && source.matches(Regex(pattern))) {
                return@InputFilter source
            } else {
                return@InputFilter ""
            }
        }

        binding.apply {
            tvNameField.text = titleField
            tiField.hint = hintField
            tiField.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            maxLines?.let {
                tiField.maxLines = it
            }
            val filters = mutableListOf<InputFilter>()

            if (allCaps) {
                filters.add(InputFilter.AllCaps())
            }

            if(restringOnlyAlphaNumericCharacters) {
                filters.add(filterOnlyAlphanumeric)
            }

            filters.add(InputFilter.LengthFilter(maxLength))

            tiField.filters = filters.toTypedArray()


            tiField.doOnTextChanged { text, _, _, _ ->
                onChange(text.toString())
            }
            prefix?.let {
                tilField.prefixText = it
            }
        }
    }

    fun setError(errorMessage: String) {
        binding.tilField.error = errorMessage
    }

    fun clearMessage() {
        binding.tilField.error = null
    }

    fun setText(text: String) {
        binding.tiField.setText(text)
    }

    fun fieldEnabled(isEnabled: Boolean) {
        binding.root.isEnabled = isEnabled
    }

}