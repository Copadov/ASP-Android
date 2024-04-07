package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.FavoriteAccountRepository
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSendMoneyViewModel(private val favoriteAccountRepository: FavoriteAccountRepository) :
    ViewModel() {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val _favoriteList = MutableLiveData<MutableList<FavoritosTransferencia>>()
    val favoriteList: LiveData<MutableList<FavoritosTransferencia>> get() = _favoriteList
    private val favoriteListCache: MutableList<FavoritosTransferencia> = mutableListOf()
    private val _uiState: MutableLiveData<UIStates<Any>> =
        MutableLiveData<UIStates<Any>>().apply {
            value = UIStates.Init
        }
    val uiState: LiveData<UIStates<Any>> get() = _uiState

    fun getFavoriteList() {
        _uiState.value = UIStates.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val result = favoriteAccountRepository.favoriteAccount()
            handleResult(result)
        }
    }

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success<Any> -> handleSuccess(result)
            is CommonStatusServiceState.Error -> handleError(result)
        }
    }

    fun handleSuccess(success: CommonStatusServiceState.Success<Any>) {
        _uiState.postValue(UIStates.Success(value = success.value as MutableList<FavoritosTransferencia>))
    }

    fun handleError(error: CommonStatusServiceState.Error) {
        _uiState.postValue(UIStates.Error(message = error.message))
    }

    private fun favoriteList() {
        favoriteListCache.addAll(account.cuenta.favoritosTransferencias.toMutableList())
        _favoriteList.value = favoriteListCache
    }

    fun queryFavorite(query: String) {
        if (query.isNotEmpty() || query.isNotBlank()) {
            _favoriteList.value =
                favoriteListCache.filter { favorite ->
                    favorite.nombreBeneficiario.lowercase().contains(query.lowercase())
                }
                    .toMutableList()
        } else {
            _favoriteList.value = favoriteListCache
        }
    }
}