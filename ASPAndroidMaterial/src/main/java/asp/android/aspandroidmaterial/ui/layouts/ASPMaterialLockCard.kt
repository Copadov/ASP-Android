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

class ASPMaterialLockCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var listener: OnLockCardListener? = null
    private var isLocked = true
    var withAutoToggle = true
    var isNewRule = false

    init {
        LayoutInflater.from(context).inflate(R.layout.asp_lock_card_layout, this, true)

        setOnClickListener {
            if (withAutoToggle) {
                toggleLock(findViewById(R.id.parentRoot))
            }
            listener?.onLockCardClicked(isLocked)
        }
    }

    fun setOnLockCardListener(listener: OnLockCardListener) {
        this.listener = listener
    }

    fun toggleLock(parentLayout: ConstraintLayout) {
        val imageViewLock = findViewById<ImageView>(R.id.imageViewLock)
        val textView = findViewById<TextView>(R.id.textViewLockStatus)

        // Clona el estado actual de ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parentLayout)

        // Modifica las propiedades layout_constraintHorizontal_bias de las vistas
        if (isLocked) {
            constraintSet.setHorizontalBias(R.id.imageViewLock, 1f)
            constraintSet.setHorizontalBias(R.id.textViewLockStatus, 1f)
            imageViewLock.setImageResource(R.drawable.ic_lock)
        } else {
            constraintSet.setHorizontalBias(R.id.imageViewLock, 0f)
            constraintSet.setHorizontalBias(R.id.textViewLockStatus, 0f)
            imageViewLock.setImageResource(R.drawable.ic_lock_open)
        }

        // Aplica el ConstraintSet actualizado al ConstraintLayout
        constraintSet.applyTo(parentLayout)

        // Actualiza el estado del indicador de visibilidad
        isLocked = !isLocked
    }

    // Agrega la propiedad para cambiar el estado isLocked desde el fragmento
    var isCardLocked: Boolean
        get() = isLocked
        set(value) {
            if (!isNewRule) {
                if (value != isLocked) {
                    isLocked = value
                    // Actualiza el estado visual en función del nuevo valor
                    val parentLayout = findViewById<ConstraintLayout>(R.id.parentRoot)
                    toggleLock(parentLayout)
                }
            } else {
                isLocked = value
                // Actualiza el estado visual en función del nuevo valor
                val parentLayout = findViewById<ConstraintLayout>(R.id.parentRoot)
                toggleLock(parentLayout)
            }
        }

    interface OnLockCardListener {
        fun onLockCardClicked(isLocked: Boolean) // Agregar el parámetro isLocked
    }
}