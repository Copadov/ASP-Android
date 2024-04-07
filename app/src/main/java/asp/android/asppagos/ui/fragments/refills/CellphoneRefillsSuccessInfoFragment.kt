package asp.android.asppagos.ui.fragments.refills

import android.os.Bundle
import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseSuccessInfoFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment
import asp.android.asppagos.utils.fromJson

class CellphoneRefillsSuccessInfoFragment: BaseSuccessInfoFragment() {

    override var TAG = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(R.string.cellphone_refills)
        getSuccessInfo()
    }

    private fun getSuccessInfo() {
        arguments?.getString(SendMoneyCodeSecurityFragment.SUCCESS_INFO_TRANSACTION)?.let {
            handleInformation(it.fromJson<SpeiDataResponse>())
        }
    }

    private fun handleInformation(transactionInfo: SpeiDataResponse) {
        setInformation(
            amount = transactionInfo.amount,
            beneficiary = transactionInfo.beneficiary ?: "",
            reference = transactionInfo.reference ?: "",
            trackingCode = transactionInfo.trackingCode ?: "",
            cellphoneNumber = transactionInfo.fifthValue,
            isPaymentServiceOperation = transactionInfo.isPaymentServicesOperation,
            isRechargeCellphone = transactionInfo.isRechargeService
        )
    }

}