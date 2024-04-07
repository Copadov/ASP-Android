package asp.android.aspandroidmaterial.ui.toolbars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import asp.android.aspandroidmaterial.R

class ASPMaterialToolbarCarDetail : LinearLayoutCompat {

    private var aspMaterialToolbarCarDetailListeners: ASPMaterialToolbarCarDetailListeners? = null

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
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.asp_car_detail_toolbar_layout, this)

        findViewById<ImageView>(R.id.backbuttonOption).setOnClickListener {
            aspMaterialToolbarCarDetailListeners?.onClickBackButton()
        }

        findViewById<ImageView>(R.id.whatsappbuttonOption).setOnClickListener {
            aspMaterialToolbarCarDetailListeners?.onClickWhatsappIcon()
        }
    }

    fun setASPMaterialToolbarCarDetailListeners(
        aspMaterialToolbarCarDetailListeners: ASPMaterialToolbarCarDetailListeners
    ) {
        this.aspMaterialToolbarCarDetailListeners = aspMaterialToolbarCarDetailListeners
    }

    interface ASPMaterialToolbarCarDetailListeners {
        fun onClickWhatsappIcon()
        fun onClickBackButton()
    }
}