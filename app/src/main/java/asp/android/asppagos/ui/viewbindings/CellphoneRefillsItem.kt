package asp.android.asppagos.ui.viewbindings

import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.CompanyCellphoneRefills
import asp.android.asppagos.databinding.ItemFavoriteServicesBinding
import coil.load
import com.xwray.groupie.viewbinding.BindableItem

class CellphoneRefillsItem(
    private val cellphoneRefill: CompanyCellphoneRefills,
    private val onTap: (CompanyCellphoneRefills) -> Unit
) :
    BindableItem<ItemFavoriteServicesBinding>() {
    override fun bind(viewBinding: ItemFavoriteServicesBinding, position: Int) {
        viewBinding.apply {
            ivService.load(cellphoneRefill.urlImage)
            tvService.text = cellphoneRefill.name
            containerFavorite.setOnClickListener {
                onTap(cellphoneRefill)
            }
        }
    }

    override fun getLayout() = R.layout.item_favorite_services

    override fun initializeViewBinding(view: View) = ItemFavoriteServicesBinding.bind(view)

}