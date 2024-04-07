package asp.android.aspandroidmaterial.ui.toolbars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import asp.android.aspandroidmaterial.R

class ASPMaterialToolbarMainDashboard : LinearLayoutCompat {

    private var aspMaterialToolbarDashboardListeners
            : ASPMaterialToolbarMainDashboardListeners? = null

    private lateinit var textTitle: TextView

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
        inflater.inflate(R.layout.asp_dashboard_main_toolbar_layout, this)

        findViewById<ImageView>(R.id.backbuttonOption).setOnClickListener {
            aspMaterialToolbarDashboardListeners?.onClickBackButton()
        }

        findViewById<ImageView>(R.id.whatsappbuttonOption).setOnClickListener {
            aspMaterialToolbarDashboardListeners?.onClickWhatsappIcon()
        }

        textTitle = findViewById<TextView>(R.id.textTitleToolbar)
    }

    fun setASPMaterialToolbarsListeners(
        aspMaterialToolbarDashboardListeners: ASPMaterialToolbarMainDashboardListeners
    ) {
        this.aspMaterialToolbarDashboardListeners = aspMaterialToolbarDashboardListeners
    }

    fun setTitle(title: String) {
        textTitle.text = title
    }

    fun isVisibleBackButton(isVisible: Boolean) {
        findViewById<ImageView>(R.id.backbuttonOption).isVisible = isVisible
    }

    interface ASPMaterialToolbarMainDashboardListeners {
        fun onClickWhatsappIcon()
        fun onClickBackButton()
    }
}