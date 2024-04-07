package asp.android.aspandroidmaterial.ui.toolbars

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.Toolbar
import asp.android.aspandroidmaterial.R

class ASPTMaterialToolbar : LinearLayoutCompat {

    private var aSPMaterialToolbarsListeners : ASPMaterialToolbarsListeners? = null

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
        inflater.inflate(R.layout.asp_toolbar_layout, this)

        var imageView = findViewById<ImageView>(R.id.backicon)

        imageView.setOnClickListener {
            aSPMaterialToolbarsListeners?.onClickBackPressed()
        }
    }

    fun setASPMaterialToolbarsListeners(aSPMaterialToolbarsListeners : ASPMaterialToolbarsListeners) {
        this.aSPMaterialToolbarsListeners = aSPMaterialToolbarsListeners
    }

    /**
     * Method to hide the back button on the toolbar.
     */
    fun hideBackIcon() {
        val imageView = findViewById<ImageView>(R.id.backicon)
        imageView.visibility = View.INVISIBLE
    }

    interface ASPMaterialToolbarsListeners {
        fun onClickBackPressed()
        fun onClickWhatsappIcon()
    }
}