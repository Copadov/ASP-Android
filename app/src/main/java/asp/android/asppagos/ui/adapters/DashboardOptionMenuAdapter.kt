package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R

class DashboardOptionMenuAdapter(
    private var menus: List<MenuOption>,
    private var listener: (TypeMenu) -> Unit
) : RecyclerView.Adapter<DashboardOptionMenuAdapter.DashboardOptionMenuAdapterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardOptionMenuAdapterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout._dashboard_item_layout,
            parent, false
        )

        return DashboardOptionMenuAdapterHolder(itemView)
    }

    override fun getItemCount() = menus.size

    override fun onBindViewHolder(holder: DashboardOptionMenuAdapterHolder, position: Int) {
        val currentItem = menus[position]
        holder.itemView.findViewById<ConstraintLayout>(R.id.option_menu).setOnClickListener {
            listener(menus[position].type)
        }

        holder.bind(currentItem)
    }

    class DashboardOptionMenuAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.findViewById<ImageView>(R.id.imageMenuIcon)
        val title = itemView.findViewById<TextView>(R.id.textViewTitle)
        fun bind(currentItem: MenuOption) {
            icon.setImageDrawable(
                AppCompatResources.getDrawable(
                    icon.context, currentItem.Icon
                )
            )
            title.text = currentItem.title
        }
    }
}

data class MenuOption(
    var title: String,
    var Icon: Int,
    var type: TypeMenu
)

enum class TypeMenu {
    UNDEFINED,
    CARDS,
    MOVEMENTS,
    CODI,
    CONFIGURATION,
    SENDMONEY,
    RECEIVEMONEY,
    PAY,
    PAYSERVICE,
    PHONERECHARGE,
    PLUS
}