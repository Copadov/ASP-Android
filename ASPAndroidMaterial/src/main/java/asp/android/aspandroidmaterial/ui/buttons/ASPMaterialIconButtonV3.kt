package asp.android.aspandroidmaterial.ui.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import asp.android.aspandroidmaterial.R

class ASPMaterialIconButtonV3 : AppCompatButton {

    var imageIcon: Int = R.drawable.arrow_btn_continuar

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(context: Context) : super(context) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        this.background = AppCompatResources.getDrawable(context, R.drawable.asp_icon_button_background_selector_v3)
        this.textSize = 12f
        this.setPadding(95, 0, 95, 0)
        this.transformationMethod = null
        this.compoundDrawablePadding = 35
        this.setTextColor(ContextCompat.getColor(context, R.color.white))
        this.typeface = ResourcesCompat.getFont(context, R.font.ubuntu_medium)
    }
}