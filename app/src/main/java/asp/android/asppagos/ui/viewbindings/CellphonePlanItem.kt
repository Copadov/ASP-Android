package asp.android.asppagos.ui.viewbindings

import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.databinding.ItemCellphonePlanBinding
import com.xwray.groupie.viewbinding.BindableItem

class CellphonePlanItem(
    private val planName: String,
    private val planSelected: CellphoneRefillServiceResponse,
    private val onPlanSelected: (CellphoneRefillServiceResponse) -> Unit
) : BindableItem<ItemCellphonePlanBinding>() {
    override fun bind(viewBinding: ItemCellphonePlanBinding, position: Int) {
        viewBinding.apply {
            tvPlanName.text = planName
            root.setOnClickListener {
                onPlanSelected.invoke(planSelected)
            }
        }
    }

    override fun getLayout() = R.layout.item_cellphone_plan

    override fun initializeViewBinding(view: View) = ItemCellphonePlanBinding.bind(view)
}