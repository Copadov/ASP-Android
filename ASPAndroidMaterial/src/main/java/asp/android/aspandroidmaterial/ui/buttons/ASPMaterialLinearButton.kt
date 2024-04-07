package asp.android.aspandroidmaterial.ui.buttons

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import asp.android.aspandroidmaterial.R

class ASPMaterialLinearButton : AppCompatButton {

    constructor(context: Context) : super(context){
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        this.background = AppCompatResources.getDrawable(context, R.drawable.asp_linear_button_background)
        this.textSize = 12f
        this.setPadding(24,0,24,0)
        this.setTextColor(context.getColor(R.color.asp_linear_button_text_color))
        this.transformationMethod = null
    }

}