package asp.android.asppagos.ui.viewmodels.cellphone_refills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.asppagos.data.models.cellphone_refill.AmountRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse

class CellphoneRefillsAddNumberViewModel : ViewModel() {

    private var cellphoneRefillServiceResponse: CellphoneRefillServiceResponse? = null
    private var cellphoneRefillAmountServiceResponse: AmountRefillServiceResponse? = null

    private var numberRecharge: String = ""
    private var numberRechargeVerification: String = ""

    private val _enabledButton: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }
    val enabledButton: LiveData<Boolean> get() = _enabledButton

    fun setupBundle(
        cellphoneRefillServiceResponse: CellphoneRefillServiceResponse?,
        amountRefillServiceResponse: AmountRefillServiceResponse?
    ) {
        this.cellphoneRefillServiceResponse = cellphoneRefillServiceResponse
        this.cellphoneRefillAmountServiceResponse = amountRefillServiceResponse
    }

    fun cellphoneRefill() = cellphoneRefillServiceResponse

    fun amountRefill() = cellphoneRefillAmountServiceResponse

    fun numberToRecharge() = numberRecharge

    fun setNumberRecharge(number: String) {
        numberRecharge = number
        checkButton()
    }

    fun setNumberRechargeVerification(number: String) {
        numberRechargeVerification = number
        checkButton()
    }

    private fun checkButton() {
        _enabledButton.value = numberRecharge == numberRechargeVerification
    }

}