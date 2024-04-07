package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R
import asp.android.asppagos.utils.formatAsMoney
import asp.android.asppagos.utils.openURLWithBrowser
import asp.android.asppagos.utils.setMaskedWebLink
import asp.android.asppagos.utils.setWebLink

class MovementsAdapter(
    private var movements: List<MovementItem>,
    private var listener: (MovementItem) -> Unit
) : RecyclerView.Adapter<MovementsAdapter.MovementsAdapterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementsAdapterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.my_movements_item_layout,
            parent, false
        )

        return MovementsAdapterHolder(itemView)
    }

    override fun getItemCount() = movements.size

    override fun onBindViewHolder(holder: MovementsAdapterHolder, position: Int) {
        val currentItem = movements[position]

        holder.itemView.setOnClickListener {
            listener(movements[position])
        }

        holder.bind(currentItem)
    }

    class MovementsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.textTitle)
        val subtitle = itemView.findViewById<TextView>(R.id.textSubtitle)
        val description = itemView.findViewById<TextView>(R.id.textDescription)
        val imageIcon = itemView.findViewById<ImageView>(R.id.imageMenuIcon)

        val amount = itemView.findViewById<TextView>(R.id.textAmount)
        val date = itemView.findViewById<TextView>(R.id.textDate)

        fun bind(currentItem: MovementItem) {

            imageIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    itemView.context,
                    when (currentItem.movementtype) {
                        MovementType.PAY -> {
                            R.drawable.movement_pay_icon
                        }

                        MovementType.RECHARGE -> {
                            R.drawable.movement_recharge_icon
                        }

                        MovementType.TRANSFER -> {
                            R.drawable.movement_transfer_icon
                        }

                        MovementType.REFUND -> {
                            R.drawable.movement_refund_icon
                        }
                    }
                )
            )

            title.text = currentItem.titlemovement
            description.text = currentItem.description

            amount.text = currentItem.amount.toString().formatAsMoney(currentItem.movementtype)
            date.text = currentItem.date

            if (!currentItem.description.isNullOrEmpty()) {
                description.visibility = View.VISIBLE
                description.text = "CEP"
                description.setOnClickListener {
                    itemView.context.openURLWithBrowser(currentItem.description)
                }
            }

            subtitle.text = currentItem.titlesubtitle

            when (currentItem.movementtype) {
                MovementType.PAY,
                MovementType.TRANSFER -> {
                    amount.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_amount_red
                        )
                    )
                }

                MovementType.RECHARGE -> {
                    amount.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_amount_red
                        )
                    )
                    subtitle.visibility = View.GONE
                }

                MovementType.REFUND -> {
                    amount.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.text_amount_green
                        )
                    )
                }
            }
        }
    }
}

data class MovementItem(
    val movementtype: MovementType = MovementType.PAY,
    val titlemovement: String = "",
    val titlesubtitle: String = "",
    val description: String = "",
    val date: String = "",
    val amount: Float = 0.0f
)

enum class MovementType {
    TRANSFER,
    PAY,
    RECHARGE,
    REFUND,
}
