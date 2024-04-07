package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import asp.android.asppagos.R
import asp.android.asppagos.ui.fragments.onboarding.FormDataUser11Fragment
import asp.android.asppagos.ui.fragments.onboarding.ValidatePassType

class RestrictionsPasswordAdapter(
    private val list: MutableList<FormDataUser11Fragment.ValidateRegexPass>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
        const val VIEW_TYPE_THREE = 3
    }

    override fun getItemCount() = list.size


    override fun getItemViewType(position: Int): Int {
        return when (list[position].Validation) {
            ValidatePassType.NOTVALIDATED -> {
                VIEW_TYPE_ONE
            }
            else -> {
                if (list[position].IsCorrect) {
                    VIEW_TYPE_TWO
                } else {
                    VIEW_TYPE_THREE
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TWO -> {
                RestrictionPasswordViewHolderCorrect(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.password_restrictions_item_correct,
                        parent, false
                    )
                )
            }
            VIEW_TYPE_THREE -> {
                RestrictionPasswordViewHolderIncorrect(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.password_restrictions_item_incorrect,
                        parent, false
                    )
                )
            }
            else -> {
                RestrictionPasswordViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.password_restrictions_item,
                        parent, false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = list[position]

        when (currentItem.Validation) {
            ValidatePassType.NOTVALIDATED -> {
                (holder as RestrictionPasswordViewHolder).bind(currentItem)
            }
            else -> {
                if (list[position].IsCorrect) {
                    (holder as RestrictionPasswordViewHolderCorrect).bind(currentItem)
                } else {
                    (holder as RestrictionPasswordViewHolderIncorrect).bind(currentItem)
                }
            }
        }
    }

    class RestrictionPasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val description: TextView = itemView.findViewById(R.id.textRestriction)
        fun bind(currentItem: FormDataUser11Fragment.ValidateRegexPass) {
            description.text = currentItem.TextDescription
        }
    }

    class RestrictionPasswordViewHolderCorrect(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val description: TextView = itemView.findViewById(R.id.textRestriction)
        fun bind(currentItem: FormDataUser11Fragment.ValidateRegexPass) {
            description.text = currentItem.TextDescription
        }
    }

    class RestrictionPasswordViewHolderIncorrect(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val description: TextView = itemView.findViewById(R.id.textRestriction)
        fun bind(currentItem: FormDataUser11Fragment.ValidateRegexPass) {
            description.text = currentItem.TextDescription
        }
    }
}