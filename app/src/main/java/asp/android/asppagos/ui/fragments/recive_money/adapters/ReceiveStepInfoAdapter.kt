package asp.android.asppagos.ui.fragments.recive_money.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.databinding.ItemReceiveMoneyBinding

class ReceiveStepInfoAdapter(private val steps: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<ReceiveStepInfoAdapter.ViewHolder>() {

    fun submit(newSteps: MutableList<String>) {
        steps.clear()
        steps.addAll(newSteps)
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemReceiveMoneyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = steps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, steps[position])
    }

    inner class ViewHolder(val binding: ItemReceiveMoneyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, text: String) {
            binding.apply {
                val stepNumber = position + 1
                tvStepNumber.text = stepNumber.toString()
                tvStepLabel.text = text
            }
        }

    }
}