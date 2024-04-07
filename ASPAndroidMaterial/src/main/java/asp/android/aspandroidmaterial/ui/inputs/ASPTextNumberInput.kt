package asp.android.aspandroidmaterial.ui.inputs

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import asp.android.aspandroidmaterial.databinding.AspTextNumberInputBinding

class ASPTextNumberInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: AspTextNumberInputBinding =
        AspTextNumberInputBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupInitialField(
        isDecimal:Boolean? = false,
        titleField: String,
        hintField: String,
        prefix: String? = null,
        maxLength: Int = 90,
        @DrawableRes drawableEnd: Int? = null,
        onChange: (textChange: String) -> Unit
    ) {
        binding.apply {
            tvNameField.text = titleField
            tiField.hint = hintField
            tiField.filters = arrayOf(InputFilter.LengthFilter(maxLength))
            if(isDecimal!!){
                tiField.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED

            }
            tiField.doOnTextChanged { text, _, _, _ ->
                if (isDecimal && !text.isNullOrBlank()) {
                    val finalAmmount = text.toString().formatAmmount(maxLength-3, 2)
                    if (finalAmmount != text.toString()) {
                        tiField.setText(finalAmmount)
                        tiField.setSelection(finalAmmount.length)
                    }
                }
                onChange(text.toString())
            }
            prefix?.let {
                tilField.prefixText = it
            }
            drawableEnd?.let {
                tiField.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(binding.root.context, it),
                    null
                )
            }
        }
    }

    fun setValue(value: String) {
        binding.apply {
            tiField.setText(value)
        }
    }
    fun setEnable(value:Boolean){
        binding.apply {
            tiField.isActivated=value
            tiField.isEnabled=value
        }
    }

    /**
     * Function to format and validate string with decimal digits.
     * Validates max digits before and after period, and also converts "." to "0.".
     *
     * @param maxBeforePoint Max digits allowed before period
     * @param maxDecimal Max decimal digits allowed
     *
     * @return formatted String
     */
    private fun String.formatAmmount(maxBeforePoint: Int, maxDecimal: Int): String {
        var str = this
        if (this.isEmpty()) {
            return this
        }
        if (this[0] == '.') {
            str = "0$this"
        }
        val inputSize = str.length
        var finalInput = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char
        while (i < inputSize) {
            t = str[i]
            if (t != '.' && !after) {
                up++
                if (up > maxBeforePoint) return finalInput
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > maxDecimal) return finalInput
            }
            finalInput += t
            i++
        }
        return finalInput
    }
}