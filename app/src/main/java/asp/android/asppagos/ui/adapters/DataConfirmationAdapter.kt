package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R
import asp.android.asppagos.data.models.DataItem

class DataConfirmationAdapter(
    private var userDataList: List<DataItem>,
    private val listener: (DataItem) -> Unit
) :
    RecyclerView.Adapter<DataConfirmationAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.data_confirmation_item,
            parent, false
        )

        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = userDataList[position]
        holder.itemView.findViewById<ImageView>(R.id.imageViewEditData).setOnClickListener {
            listener(userDataList[position])
        }
        holder.bind(currentItem)
    }

    fun updateListItems(list: List<DataItem>) {
        userDataList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = userDataList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleV = itemView.findViewById<TextView>(R.id.TextviewTitle)
        val textV = itemView.findViewById<TextView>(R.id.TextViewText)
        fun bind(currentItem: DataItem) {
            titleV.text = currentItem.name
            textV.text = currentItem.value
        }
    }
}