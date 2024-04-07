package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import asp.android.asppagos.data.repositories.CheckUserPinRepository
import asp.android.asppagos.ui.states.UIStates

/**
 * ViewModel to handle the biometric authentication.
 *
 * @param pinRepository Repository to consume services for authentication.
 */
class PinInputCodeSecurityViewModel(pinRepository: CheckUserPinRepository): CodeSecurityBaseViewModel(pinRepository) {

    /**
     * LiveData to manage UI states.
     */
    private val _uiState: MutableLiveData<UIStates<Boolean>> =
        MutableLiveData<UIStates<Boolean>>().apply { value = UIStates.Init }

    val uiState: LiveData<UIStates<Boolean>> get() = _uiState


    override fun handleResult(state: CodeSecurityState) {
        when (state) {
            is CodeSecurityState.Loading -> _uiState.postValue(UIStates.Loading)
            is CodeSecurityState.Success -> _uiState.postValue(UIStates.Success(value = true))
            is CodeSecurityState.Error -> _uiState.postValue(UIStates.Error(message = state.message))
        }
    }

    fun resetUiState() {
        _uiState.value = UIStates.Init
    }
}