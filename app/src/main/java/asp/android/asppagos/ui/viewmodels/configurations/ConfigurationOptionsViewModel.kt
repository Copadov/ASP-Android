package asp.android.asppagos.ui.viewmodels.configurations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.CodiFavoriteAccountRepository
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.utils.CODI_FAVORITE_ACCOUNT
import asp.android.asppagos.utils.EncryptUtils.generaRegistraAppPorOmision
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfigurationOptionsViewModel(private val codiFavoriteAccountRepository: CodiFavoriteAccountRepository) :
    ViewModel() {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val _uiState: MutableLiveData<UIStates<Any>> =
        MutableLiveData<UIStates<Any>>().apply {
            value = UIStates.Init
        }
    val uiState: LiveData<UIStates<Any>> get() = _uiState

    fun codiFavoriteAccount() {
        _uiState.value = UIStates.Loading
        val dvProperty = Prefs.get("dv", "")
        val ncProperty = Prefs.get("aliasNC", "")
        if (dvProperty.isNullOrEmpty().not() && ncProperty.isNullOrEmpty().not()) {
            val hcmacProperty = generaRegistraAppPorOmision(
                dvProperty = dvProperty.toInt(),
                ncProperty = ncProperty,
                cellphone = account.cuenta.telefono
            )
            viewModelScope.launch(Dispatchers.IO) {
                hcmacProperty?.let {
                    val result = codiFavoriteAccountRepository.codiFavoriteAccount(String.format("%03d", dvProperty.toInt()), hcmacProperty, ncProperty)
                    handleResult(result)
                }
            }
        } else {
            _uiState.value =
                UIStates.Error(message = "Se debe realizar el enrolamiento de la cuenta")
        }
    }

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success -> handleSuccess(result)
            is CommonStatusServiceState.Error -> handleError(result)
        }
    }

    private fun handleSuccess(success: CommonStatusServiceState.Success<Any>) {
        saveCodiFavoriteSuccess()
        _uiState.postValue(UIStates.Success(value = null))
    }

    private fun saveCodiFavoriteSuccess() {
        Prefs.set(CODI_FAVORITE_ACCOUNT,true)
    }

    fun getIsCodiFavorite() = Prefs.get(CODI_FAVORITE_ACCOUNT, false)

    private fun handleError(error: CommonStatusServiceState.Error) {
        _uiState.postValue(UIStates.Error(message = error.message))
    }

}