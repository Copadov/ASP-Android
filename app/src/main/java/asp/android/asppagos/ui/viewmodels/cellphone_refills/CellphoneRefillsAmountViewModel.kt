package asp.android.asppagos.ui.viewmodels.cellphone_refills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.asppagos.data.models.cellphone_refill.AmountRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.ui.states.UIStates

class CellphoneRefillsAmountViewModel : ViewModel() {

    private val _uiState: MutableLiveData<UIStates<MutableList<AmountRefillServiceResponse>>> =
        MutableLiveData<UIStates<MutableList<AmountRefillServiceResponse>>>().apply {
            value = UIStates.Init
        }
    val uiState: LiveData<UIStates<MutableList<AmountRefillServiceResponse>>> get() = _uiState

    private var cellphoneRefillServiceResponse: CellphoneRefillServiceResponse? = null

    fun setupBundle(cellphoneRefillServiceResponse: CellphoneRefillServiceResponse?) {
        this.cellphoneRefillServiceResponse = cellphoneRefillServiceResponse
        setupAmountList()
    }

    private fun setupAmountList() {
        _uiState.value = UIStates.Success<MutableList<AmountRefillServiceResponse>>(
            value = cellphoneRefillServiceResponse?.amountList?.filterNotNull()?.toMutableList() ?: mutableListOf()
        )
    }

    fun cellphoneRefill() = cellphoneRefillServiceResponse

}