package asp.android.aspandroidmaterial.ui.spinners

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatSpinner
import asp.android.aspandroidmaterial.R

class ASPMaterialSpinnerCountryCode : AppCompatSpinner {

    constructor(context: Context) : super(context) {
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
        this.background =
            AppCompatResources.getDrawable(context, R.drawable.asp_spinner_country_code_background)
        this.minimumHeight = 140
    }
}