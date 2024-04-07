package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R
import asp.android.asppagos.utils.copyTextToClipboard
import asp.android.asppagos.utils.copyToClipboard

class AccountDetailOptionsAdapter(
    private var menus: List<AccountOption>,
    private var listener: (AccountOption) -> Unit
) : RecyclerView.Adapter<AccountDetailOptionsAdapter.AccountDetailOptionsAdapterHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountDetailOptionsAdapterHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.general_account_detail_item,
            parent, false
        )

        return AccountDetailOptionsAdapterHolder((itemView))
    }

    override fun getItemCount() = menus.size

    override fun onBindViewHolder(holder: AccountDetailOptionsAdapterHolder, position: Int) {
        val currentItem = menus[position]
        holder.itemView.findViewById<ImageView>(R.id.copyTextOption).setOnClickListener {
            listener(menus[position])
        }

        holder.bind(currentItem)
    }

    class AccountDetailOptionsAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.textTitle)
        val description = itemView.findViewById<TextView>(R.id.textTitleDescription)
        val copyOption = itemView.findViewById<ImageView>(R.id.copyTextOption)

        fun bind(currentItem: AccountOption) {
            copyOption.setOnClickListener {
                description.copyToClipboard()
            }
            title.text = currentItem.titleText
            description.text = currentItem.titleDescription
            copyOption.isVisible = currentItem.isVisibleCopy
        }
    }
}

data class AccountOption(
    val titleText: String,
    val titleDescription: String,
    val isVisibleCopy: Boolean
)
