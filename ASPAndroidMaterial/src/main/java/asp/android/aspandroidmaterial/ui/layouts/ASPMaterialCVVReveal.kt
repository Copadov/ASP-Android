package asp.android.aspandroidmaterial.ui.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import asp.android.aspandroidmaterial.R

class ASPMaterialCVVReveal @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var listener: OnRevealCVVListener? = null
    private var isCVVVisible = true

    init {
        LayoutInflater.from(context).inflate(R.layout.asp_cvv_reveal_layout, this, true)

        setOnClickListener {
            revealCVV(findViewById(R.id.parentRoot))
            listener?.onRevealCVVClicked()
        }
    }

    fun setOnRevealCVVListener(listener: OnRevealCVVListener) {
        this.listener = listener
    }

    /**
     * Function to restart the [ASPMaterialCVVReveal] component to
     * its original state, hiding the CVV value.
     */
    fun restart() {
        isCVVVisible = false
        revealCVV(findViewById(R.id.parentRoot))
    }

    fun revealCVV(parentLayout: ConstraintLayout) {
        val imageViewDot = findViewById<ImageView>(R.id.imageViewDot)
        val textView = findViewById<TextView>(R.id.textViewCVV)

        // Clona el estado actual de ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)

        // Modifica las propiedades layout_constraintHorizontal_bias de las vistas
        if (isCVVVisible) {
            constraintSet.setHorizontalBias(R.id.imageViewDot, 0f)
            constraintSet.setHorizontalBias(R.id.textViewCVV, 1f)
            imageViewDot.setImageResource(R.drawable.circle_cvv_green)
        } else {
            constraintSet.setHorizontalBias(R.id.imageViewDot, 1f)
            constraintSet.setHorizontalBias(R.id.textViewCVV, 0f)
            imageViewDot.setImageResource(R.drawable.circle_cvv)
        }

        // Aplica el ConstraintSet actualizado al ConstraintLayout
        constraintSet.applyTo(parentLayout)

        // Actualiza el estado del indicador de visibilidad
        isCVVVisible = !isCVVVisible
    }

    interface OnRevealCVVListener {
        fun onRevealCVVClicked()
    }
}