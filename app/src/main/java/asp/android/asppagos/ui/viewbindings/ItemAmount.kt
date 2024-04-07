package asp.android.asppagos.ui.viewbindings

import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.cellphone_refill.AmountRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.databinding.ItemAmountCellphoneRefillsBinding
import com.xwray.groupie.viewbinding.BindableItem

class ItemAmount(
    private val amountRefillServiceResponse: AmountRefillServiceResponse,
    private val onTapAmount: (AmountRefillServiceResponse) -> Unit
) :
    BindableItem<ItemAmountCellphoneRefillsBinding>() {

    override fun bind(viewBinding: ItemAmountCellphoneRefillsBinding, position: Int) {
        viewBinding.apply {
            tvAmount.text = "$${amountRefillServiceResponse.amount?.toInt()}"
            viewBinding.root.setOnClickListener {
                onTapAmount.invoke(amountRefillServiceResponse)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_amount_cellphone_refills

    override fun initializeViewBinding(view: View) = ItemAmountCellphoneRefillsBinding.bind(view)
}