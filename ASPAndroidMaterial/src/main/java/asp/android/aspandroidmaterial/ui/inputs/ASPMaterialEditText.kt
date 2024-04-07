package asp.android.aspandroidmaterial.ui.inputs

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import asp.android.aspandroidmaterial.R

class ASPMaterialEditText : AppCompatEditText {

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
        this.background = AppCompatResources.getDrawable(context, R.drawable.asp_edittext_background_selector)
        this.setPadding(60,0,60,0)
        this.setHintTextColor(context.getColor(R.color.asp_edittext_hint_color))
        this.setTextColor(context.getColor(R.color.asp_edittext_text_color))
        this.height = 140
        this.textSize = 12f
    }
}