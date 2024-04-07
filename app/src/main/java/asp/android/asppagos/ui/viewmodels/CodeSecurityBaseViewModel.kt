package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.CheckUserPinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class CodeSecurityBaseViewModel(private val pinRepository: CheckUserPinRepository) :
    ViewModel() {

    fun checkPinUser(pinUser: String = "", pinRequestTypeTransaction: PinRequestTypeTransaction, isBiometric: Boolean = false) {
        handleResult(CodeSecurityState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(pinRepository.checkUserPin(pinUser, pinRequestTypeTransaction, isBiometric))
        }
    }

    private fun handleResult(state: CommonStatusServiceState<Any>) {
        when (state) {
            is CommonStatusServiceState.Success -> handleResult(CodeSecurityState.Success)
            is CommonStatusServiceState.Error -> handleResult(CodeSecurityState.Error(message = state.message))
        }
    }


    abstract fun handleResult(state: CodeSecurityState)

}


sealed class CodeSecurityState {
    object Loading: CodeSecurityState()
    object Success : CodeSecurityState()
    class Error(val message: String) : CodeSecurityState()
}