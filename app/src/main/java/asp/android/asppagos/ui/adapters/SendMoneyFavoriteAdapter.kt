package asp.android.asppagos.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import asp.android.aspandroidmaterial.R as MaterialR
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.databinding.SendMoneyFavoriteRowBinding
import asp.android.asppagos.utils.getFirstLetters

class SendMoneyFavoriteAdapter(
    private val favoriteAccountList: MutableList<FavoritosTransferencia> = mutableListOf(),
    private val tapFavorite: (favorite: FavoritosTransferencia) -> Unit
) :
    RecyclerView.
    Adapter<SendMoneyFavoriteAdapter.ViewHolder>() {

    fun submitAll(newFavoriteAccountList: MutableList<FavoritosTransferencia>) {
        favoriteAccountList.clear()
        favoriteAccountList.addAll(newFavoriteAccountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(SendMoneyFavoriteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = favoriteAccountList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(favoriteAccountList[position], position)
    }

    inner class ViewHolder(val binding: SendMoneyFavoriteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoritosTransferencia, position: Int) {
            binding.apply {
                tvFullName.text = favorite.nombreBeneficiario
                tvBankName.text = favorite.nombreInstitucion
                cFavorite.setOnClickListener {
                    tapFavorite.invoke(favorite)
                }
                tvFullNameFirstLetters.text = favorite.nombreBeneficiario.getFirstLetters().uppercase()
            }
        }
    }

    private fun getBackgroundByPosition(position: Int): Int {
        return if ((position + 1)%2 == 0 ) MaterialR.color.color_7993BE else MaterialR.color.color_57638D
    }
}