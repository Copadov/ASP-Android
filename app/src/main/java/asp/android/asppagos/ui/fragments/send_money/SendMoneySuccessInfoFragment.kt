package asp.android.asppagos.ui.fragments.send_money

import android.os.Bundle
import android.util.Log
import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseSuccessInfoFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment.Companion.SUCCESS_INFO_TRANSACTION
import asp.android.asppagos.utils.fromJson
import java.text.DecimalFormat

class SendMoneySuccessInfoFragment : BaseSuccessInfoFragment() {

    override var TAG: String = this.javaClass.name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(R.string.send_money)
        getSuccessInfo()
    }

    private fun getSuccessInfo() {
        arguments?.getString(SUCCESS_INFO_TRANSACTION)?.let {
            handleInformation(it.fromJson<SpeiDataResponse>())
        }
    }

    private fun handleInformation(transactionInfo: SpeiDataResponse) {
        setInformation(
            amount = transactionInfo.amount,
            beneficiary = transactionInfo.beneficiary ?: "",
            reference = transactionInfo.reference ?: "",
            trackingCode = transactionInfo.trackingCode ?: "",
            isPaymentServiceOperation = transactionInfo.isPaymentServicesOperation
        )
    }


}