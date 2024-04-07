package asp.android.asppagos.ui.viewmodels.configurations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.UpdateProfileRepository
import asp.android.asppagos.ui.states.UIStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdatePersonalCodeSmsViewModel(private val updateProfileRepository: UpdateProfileRepository) :
    ViewModel() {

    private val _uiState: MutableLiveData<UIStates<String>> =
        MutableLiveData<UIStates<String>>().apply {
            value = UIStates.Init
        }
    val uiState: LiveData<UIStates<String>> get() = _uiState

    private var pinCode: String = ""
    private var isSuccessSmsCode: Boolean = false

    fun setupPinCode(pinCode: String) {
        this.pinCode = pinCode
    }

    fun sendSms() {
        viewModelScope.launch(Dispatchers.IO) {
            handleResultSendSms(updateProfileRepository.sendCode())
        }
    }

    fun handleResultSendSms(state: CommonStatusServiceState<Any>) {
        when(state) {
            is CommonStatusServiceState.Success -> isSuccessSmsCode = true
            is CommonStatusServiceState.Error -> handleError(state)
        }
    }

    private fun handleSuccessSms() {
        viewModelScope.launch(Dispatchers.IO) {
            handleUpdateCode(updateProfileRepository.updateCodeSecurity(pinCode))
        }
    }

    private fun handleUpdateCode(state: CommonStatusServiceState<Any>) {
        when(state) {
            is CommonStatusServiceState.Success -> handleSuccessUpdateCode()
            is CommonStatusServiceState.Error -> handleError(state)
        }
    }

    private fun handleSuccessUpdateCode() {
        _uiState.postValue(UIStates.Success(value = null))
    }

    private fun handleVerificationSmsCode(state: CommonStatusServiceState<Any>) {
        when(state) {
            is CommonStatusServiceState.Success -> handleSuccessSms()
            is CommonStatusServiceState.Error -> handleError(state)
        }
    }

    private fun handleError(error: CommonStatusServiceState.Error) {
        _uiState.postValue(UIStates.Error(message = error.message))
    }

    fun validateSMSCode(smsCode: String) {
        _uiState.value = UIStates.Loading
        viewModelScope.launch(Dispatchers.IO) {
            handleVerificationSmsCode(updateProfileRepository.validatePhoneWithSmsCode(smsCode))
        }
    }

    fun restartState() {
        _uiState.value = UIStates.Init
    }
}