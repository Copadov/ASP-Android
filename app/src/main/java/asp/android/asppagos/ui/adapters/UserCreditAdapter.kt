package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import asp.android.aspandroidcore.utils.getAmountFormat
import asp.android.asppagos.R
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.databinding.ItemUserCreditBinding
import asp.android.asppagos.utils.hide
import asp.android.asppagos.utils.show
import java.text.DecimalFormat

class UserCreditAdapter(
    val userCredits: MutableList<CreditosActivo> = mutableListOf(),
    val onTapCredit: (credit: CreditosActivo) -> Unit
) :
    RecyclerView.Adapter<UserCreditAdapter.ViewHolder>() {

    fun submit(newUserCredits: MutableList<CreditosActivo>) {
        userCredits.clear()
        userCredits.addAll(newUserCredits)
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemUserCreditBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = userCredits.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(credit = userCredits[position], userCredits.size == (position + 1))
    }

    inner class ViewHolder(val binding: ItemUserCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(credit: CreditosActivo, isLastItem: Boolean) {
            binding.apply {
                tvName.text = credit.control
                tvAmount.text =
                    root.resources.getString(R.string.amount, credit.monto.getAmountFormat())
                cUserCredit.setOnClickListener {
                    onTapCredit(credit)
                }
                if (isLastItem.not()) {
                    divider.show()
                } else {
                    divider.hide()
                }
            }
        }
    }
}