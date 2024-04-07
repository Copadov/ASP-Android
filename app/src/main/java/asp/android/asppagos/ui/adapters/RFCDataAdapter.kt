package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R

class RFCDataAdapter(
    private var userDataList: List<Pair<String, String>>,
    private val listener: (Pair<String, String>) -> Unit
) :
    RecyclerView.Adapter<RFCDataAdapter.RFCViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RFCViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.data_confirmation_item,
            parent, false
        )

        return RFCViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RFCViewHolder, position: Int) {
        val currentItem = userDataList[position]
        holder.itemView.findViewById<ImageView>(R.id.imageViewEditData).setOnClickListener {
            listener(userDataList[position])
        }
        holder.bind(currentItem)
    }

    fun updateListItems(list: List<Pair<String, String>>) {
        userDataList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = userDataList.size

    class RFCViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleV = itemView.findViewById<TextView>(R.id.TextviewTitle)
        val textV = itemView.findViewById<TextView>(R.id.TextViewText)
        val imageV = itemView.findViewById<ImageView>(R.id.imageViewEditData)

        fun bind(currentItem: Pair<String, String>) {
            titleV.text = currentItem.first
            textV.text = currentItem.second
            imageV.visibility = View.INVISIBLE
        }
    }
}