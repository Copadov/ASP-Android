package asp.android.aspandroidmaterial.ui.buttons

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import asp.android.aspandroidmaterial.R

class ASPMaterialIconButton : AppCompatButton {

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
        this.background =
            AppCompatResources.getDrawable(context, R.drawable.asp_icon_button_background_selector_v2)
        this.textSize = 12f
        this.setPadding(16, 0, 16, 0)
        this.transformationMethod = null
        this.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            AppCompatResources.getDrawable(context, R.drawable.arrow_btn_continuar_selector_v2),
            null
        )
        this.compoundDrawablePadding = 35
        this.typeface = ResourcesCompat.getFont(context, R.font.ubuntu_regular)
    }
}