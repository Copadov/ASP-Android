package asp.android.aspandroidmaterial.ui.inputs

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import asp.android.aspandroidmaterial.databinding.AspTextInputNumberWithIconsBinding

class ASPTextNumberInputInfo @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: AspTextInputNumberWithIconsBinding =
        AspTextInputNumberWithIconsBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupInitialField(
        titleField: String,
        hintField: String,
        maxLength: Int = 30,
        showInfoIcon: Boolean = true,
        showScanIcon: Boolean = true,
        onChange: (textChange: String) -> Unit,
        tapInfo: () -> Unit,
        tapEndIcon: () -> Unit,
    ) {
        binding.apply {
            tvNameField.text = titleField
            tiField.hint = hintField
            tiField.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            tiField.doOnTextChanged { text, _, _, _ ->
                onChange(text.toString())
            }
            ivInfoIcon.setOnClickListener {
                tapInfo.invoke()
            }
            ivEndIcon.setOnClickListener {
                tapEndIcon.invoke()
            }
            ivInfoIcon.isVisible = showInfoIcon
            ivEndIcon.isVisible = showScanIcon
        }
    }

    fun setValue(value: String) {
        binding.apply {
            tiField.setText(value)
        }
    }

}