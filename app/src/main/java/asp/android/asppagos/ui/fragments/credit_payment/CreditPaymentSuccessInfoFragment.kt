package asp.android.asppagos.ui.fragments.credit_payment

import android.os.Bundle
import android.util.Log
import android.view.View
import asp.android.asppagos.R
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.ui.fragments.BaseSuccessInfoFragment
import asp.android.asppagos.ui.fragments.send_money.SendMoneyCodeSecurityFragment
import asp.android.asppagos.utils.fromJson
import java.text.DecimalFormat

class CreditPaymentSuccessInfoFragment : BaseSuccessInfoFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(R.string.credit_payment_toolbar_title)
        getSuccessInfo()
    }

    override var TAG: String = this.javaClass.name

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
            isPaymentCreditFlow = true,
            isPaymentServiceOperation = transactionInfo.isPaymentServicesOperation
        )
    }

}