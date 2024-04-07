package asp.android.asppagos.ui.viewmodels.payment_services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.repositories.PaymentServiceRepository
import asp.android.asppagos.ui.states.UIStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentServicesMainViewModel(private val repository: PaymentServiceRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UIStates<MutableList<ServiceDataResponse>>>()
    val uiState: LiveData<UIStates<MutableList<ServiceDataResponse>>> get() = _uiState

    private val favoriteList: MutableList<ServiceDataResponse> = mutableListOf()
    private val serviceCacheList: MutableList<ServiceDataResponse> = mutableListOf()

    private val _favoriteListObservable = MutableLiveData<MutableList<ServiceDataResponse>>()
    val favoriteListObservable: LiveData<MutableList<ServiceDataResponse>> get() = _favoriteListObservable
    var hasQuery: Boolean = false

    private val _hideKeyword = MutableLiveData<Boolean>()
    val hideKeyword: LiveData<Boolean> get() = _hideKeyword

    /**
     * Manages if view is currently showing the favorites list.
     */
    var isShowingFavorites = false

    fun paymentServices() {
        _uiState.value = UIStates.Loading
        viewModelScope.launch(Dispatchers.IO) {
            handleResult(repository.getServiceList())
        }
    }

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success -> handleSuccess(result as CommonStatusServiceState.Success<List<ServiceDataResponse>>)
            is CommonStatusServiceState.Error -> handleError(result.message)
        }
    }

    private fun handleSuccess(success: CommonStatusServiceState.Success<List<ServiceDataResponse>>) {
        val successMap = (success.value as List<ServiceDataResponse>).toMutableList()
        serviceCacheList.apply {
            clear()
            addAll(successMap)
        }
        val favoriteList = successMap.filter { it.esFavorito == true }
        this.favoriteList.clear()
        this.favoriteList.addAll(favoriteList)
        _uiState.postValue(UIStates.Success<MutableList<ServiceDataResponse>>(value = successMap))
        _favoriteListObservable.postValue(this.favoriteList)
    }

    fun hasFavoriteListItems() = favoriteList.isNotEmpty()

    fun searchService(query: String) {
        if (query.isNotEmpty() || query.isNotBlank()) {
            hasQuery = true
            val filteredService = serviceCacheList.filter { service -> service.servicio?.lowercase()?.contains(query.lowercase()) == true }
            _uiState.postValue(UIStates.Success<MutableList<ServiceDataResponse>>(value = filteredService.toMutableList()))
            _hideKeyword.value = false
        } else {
            hasQuery = false
            _hideKeyword.value = true
            _uiState.postValue(UIStates.Success<MutableList<ServiceDataResponse>>(value = serviceCacheList))
        }
    }

    /**
     * Method to update the current visible list.
     *
     * @param isShowing: Boolean, true if favorites list is visible, false otherwise.
     */
    fun setIsShowingFavorites(isShowing: Boolean) {
        isShowingFavorites = isShowing
    }

    private fun handleError(message: String) {
        _uiState.postValue(UIStates.Error(message = message))
    }
}