package asp.android.aspandroidmaterial.ui.toolbars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import asp.android.aspandroidmaterial.R

class ASPMaterialToolbarDashboard : LinearLayoutCompat {

    private var aspMaterialToolbarDashboardListeners
            : ASPMaterialToolbarDashboardListeners? = null

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
        inflater.inflate(R.layout.asp_dashboard_toolbar_layout, this)

        findViewById<ImageView>(R.id.whatsappOption).setOnClickListener {
            aspMaterialToolbarDashboardListeners?.onClickWhatsappIcon()
        }

        findViewById<ImageView>(R.id.notificationOption).setOnClickListener {
            aspMaterialToolbarDashboardListeners?.onClickNotificationsIcon()
        }

        findViewById<ImageView>(R.id.imageUser).setOnClickListener {
            aspMaterialToolbarDashboardListeners?.onClickProfilePicture()
        }
    }

    fun setASPMaterialToolbarsListeners(
        aspMaterialToolbarDashboardListeners: ASPMaterialToolbarDashboardListeners
    ) {
        this.aspMaterialToolbarDashboardListeners = aspMaterialToolbarDashboardListeners
    }

    interface ASPMaterialToolbarDashboardListeners {
        fun onClickProfilePicture()
        fun onClickWhatsappIcon()
        fun onClickNotificationsIcon()
    }
}