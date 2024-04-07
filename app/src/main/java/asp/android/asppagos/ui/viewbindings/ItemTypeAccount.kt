package asp.android.asppagos.ui.viewbindings

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import asp.android.asppagos.R
import asp.android.asppagos.databinding.ItemFavoriteServicesBinding
import asp.android.asppagos.databinding.ItemTypeAccountBinding
import com.xwray.groupie.viewbinding.BindableItem

class ItemTypeAccount(
    private val typeName: String,
    private val isBusiness: Boolean = false,
    private val onTapContainer: (Boolean) -> Unit
) :
    BindableItem<ItemTypeAccountBinding>() {
    override fun bind(viewBinding: ItemTypeAccountBinding, position: Int) {
        viewBinding.apply {
            tvName.text = typeName
            cUserCredit.setOnClickListener {
                onTapContainer.invoke(isBusiness)
            }
            divider.isVisible = 1 != position
        }
    }

    override fun getLayout(): Int = R.layout.item_type_account

    override fun initializeViewBinding(view: View): ItemTypeAccountBinding =
        ItemTypeAccountBinding.bind(view)
}