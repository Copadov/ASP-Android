package asp.android.asppagos.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import asp.android.asppagos.R
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.utils.formatAsMoney
import asp.android.asppagos.utils.formatCurrency
import asp.android.asppagos.utils.mask

class CodiMovRecViewAdapter(private val transacctions:MutableList<CodiMovResponse> = mutableListOf(), private val context:Context):
    RecyclerView.Adapter<CodiMovRecViewAdapter.CodiMovViewHolder>() {
    private var clickListener:ClickListener<CodiMovResponse>? = null

    fun submitList(newTransactionList: MutableList<CodiMovResponse>) {
        transacctions.clear()
        transacctions.addAll(newTransactionList)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodiMovViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_codi_mov,parent,false)
        return CodiMovViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transacctions.size
    }

    override fun onBindViewHolder(holder: CodiMovViewHolder, position: Int) {
        val transaction=transacctions[position]
        transaction.type.let {
            if (it.equals("-")){
                holder.image.setBackgroundResource(R.drawable.asp_codi_mov_negative)
            }else if (it.equals("+")){
                holder.image.setBackgroundResource(R.drawable.asp_codi_mov_positive)
            }else{
                holder.image.setBackgroundResource(R.drawable.asp_codi_mov_dev)
            }
        }
        transaction.color.let {
            if(it.equals("verde",true)){
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.text_amount_green))
            }else if(it.equals("amarillo",true)){
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.text_amount_yellow))
            }else{
                holder.amount.setTextColor(ContextCompat.getColor(context, R.color.text_amount_red))
            }
        }
        if(transaction.status.equals("pendiente",true)){
            holder.image.setBackgroundResource(R.drawable.asp_codi_mov_pend)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.link_color))
        }
        if (transaction.status.equals("rechazado", true)) {
            holder.image.setBackgroundResource(R.drawable.asp_codi_mov_rejected)
        }
        transaction.processDate.let {
            holder.date.text = it
        }
        holder.name.text = transaction.concept
        holder.amount.text = ""+transaction.amount.toString().formatAsMoney(MovementType.TRANSFER)
        holder.view.setOnClickListener {
             clickListener!!.onItemClick(transaction)
        }

    }

    fun setOnClickListener(cardListener: ClickListener<CodiMovResponse>?){
        clickListener = cardListener
    }
    inner class CodiMovViewHolder(itemView:View): ViewHolder(itemView){
        val date:TextView = itemView.findViewById(R.id.transaction_item_date)
        val name:TextView = itemView.findViewById(R.id.transaction_item_name)
        val amount:TextView = itemView.findViewById(R.id.transaction_item_amount)
        val image:ImageView = itemView.findViewById(R.id.transaction_item_state)
        val view:CardView = itemView.findViewById(R.id.transaction_item_main)
    }
}

interface ClickListener<T>{
    fun onItemClick(data:T)
}