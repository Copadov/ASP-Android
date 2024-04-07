package asp.android.asppagos.ui.viewmodels.cellphone_refills

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.data.models.cellphone_refill.CompanyCellphoneRefills
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.CellphoneRefillsRepository
import asp.android.asppagos.ui.states.UIStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CellphoneRefillsViewModel(private val cellphoneRefillsRepository: CellphoneRefillsRepository) :
    ViewModel() {

    private val _uiState: MutableLiveData<UIStates<MutableList<CompanyCellphoneRefills>>> =
        MutableLiveData<UIStates<MutableList<CompanyCellphoneRefills>>>().apply {
            value = UIStates.Init
        }
    val uiState: LiveData<UIStates<MutableList<CompanyCellphoneRefills>>> get() = _uiState

    fun getCellphoneRefills() {
        _uiState.value = UIStates.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val result = cellphoneRefillsRepository.getCellphoneRefills()
            handleResponse(result)
        }
    }

    private fun handleResponse(state: CommonStatusServiceState<Any>) {
        when (state) {
            is CommonStatusServiceState.Success -> handleSuccessState(state.value as MutableMap<String, List<CellphoneRefillServiceResponse>>)
            is CommonStatusServiceState.Error -> handleError(state.message)
        }
    }

    private fun handleSuccessState(successState: MutableMap<String, List<CellphoneRefillServiceResponse>>) {
        val companyCellphoneRefills = mutableListOf<CompanyCellphoneRefills>()
        successState.forEach { (key, value) ->
            companyCellphoneRefills.add(
                CompanyCellphoneRefills(
                    name = key,
                    urlImage = value.first().url ?: "",
                    planList = value.toMutableList()
                )
            )
        }
        _uiState.postValue(UIStates.Success(value = companyCellphoneRefills))
    }

    private fun handleError(message: String) {
        _uiState.postValue(UIStates.Error(message))
    }
}