package asp.android.asppagos.ui.viewbindings

import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.databinding.ItemFavoriteServicesBinding
import asp.android.asppagos.ui.fragments.payment_services.ui_models.FavoriteServicesUIModel
import coil.load
import com.xwray.groupie.viewbinding.BindableItem

class FavoriteServicesRow(
    private val favoriteService: ServiceDataResponse,
    private val onTapFavorite: (ServiceDataResponse) -> Unit
) :
    BindableItem<ItemFavoriteServicesBinding>() {
    override fun bind(viewBinding: ItemFavoriteServicesBinding, position: Int) {
        viewBinding.apply {
            ivService.load(favoriteService.url)
            tvService.text = favoriteService.servicio
            containerFavorite.setOnClickListener {
                onTapFavorite(favoriteService)
            }
        }
    }

    override fun getLayout() = R.layout.item_favorite_services

    override fun initializeViewBinding(view: View) = ItemFavoriteServicesBinding.bind(view)

}