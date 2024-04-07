package asp.android.asppagos.ui.viewbindings

import android.util.Log
import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.databinding.ItemServiceRowBinding
import com.xwray.groupie.viewbinding.BindableItem

class ServiceItemRow(private val serviceDataResponse: ServiceDataResponse, val onTapService: (ServiceDataResponse) -> Unit) :
    BindableItem<ItemServiceRowBinding>() {
    override fun bind(viewBinding: ItemServiceRowBinding, position: Int) {
        viewBinding.apply {
            tvServiceName.text = serviceDataResponse.servicio
            containerParent.setOnClickListener {
                onTapService(serviceDataResponse)
            }
        }
    }

    override fun getLayout() = R.layout.item_service_row

    override fun initializeViewBinding(view: View) = ItemServiceRowBinding.bind(view)
}