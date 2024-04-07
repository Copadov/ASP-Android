import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CifDataRequest
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.DateDataRequest
import asp.android.asppagos.data.models.send_money.HeaderData
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.data.repositories.CheckUserPinRepository
import asp.android.asppagos.data.repositories.SpeiRepository
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.CodeSecurityBaseViewModel
import asp.android.asppagos.ui.viewmodels.CodeSecurityState
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreditPaymentCodeSecurityViewModel(private val repository: SpeiRepository, private val pinRepository: CheckUserPinRepository) : CodeSecurityBaseViewModel(pinRepository) {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private var creditPayment: CreditosActivo? = null
    private var amountToPayment: String? = null

    private val _uiState: MutableLiveData<UIStates<SpeiDataResponse>> =
        MutableLiveData<UIStates<SpeiDataResponse>>().apply { value = UIStates.Init }
    val uiState: LiveData<UIStates<SpeiDataResponse>> get() = _uiState

    /**
     * Variable to save device location for transactions.
     */
    private lateinit var location: Location

    /**
     * Function to update the current device location.
     *
     * @param location Current location.
     */
    fun setLocation(location: Location) {
        this.location = location
    }

    fun paymentCredit() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.sendSpeiTransaction(
                CifDataRequest(
                    paymentConcept = "Pago de crédito ${creditPayment?.control}",
                    beneficiaryEmail = "",
                    beneficiaryAccount = creditPayment?.cuentaClabe ?: "",
                    payerAccount = account.cuenta.clabe,
                    createDate = System.currentTimeMillis(),
                    bornDate = DateDataRequest(day = "26", month = "12", year = "1963"), // FALTA ESTE
                    beneficiaryInstitutionID = 90659, //ESTATICO NUNCA SE MUEVE
                    payerInstitutionID = 90659, //ESTATICO NUNCA SE MUEVE
                    beneficiaryTypeAccountID = 40, // FALTA ESTE
                    payerTypeAccountID = 40, // TRANSACCION POR CLABE 40 POR CLABE
                    paymentTypeID = 1, // SE QUEDA COMO UNO
                    amount = amountToPayment?.toDouble() ?: 0.0,
                    beneficiaryName = "Crédito: ${creditPayment?.control}", // FALTA ESTE
                    payerName = account.nombre,
                    beneficiaryRFC = ""
                ),
                HeaderData(
                    sessionId = 0L,
                    companyId = 0L,
                    responsibilityId = 0L,
                    userKey = "",
                    userId = 0L,
                    channelClassId = 1L,
                    channelId = 0L,
                    servicePointId = 0L,
                    locationId = 0L,
                    branchId = 0L,
                    commissionAgentId = 0L,
                    transactionId = 0L,
                    hostIp = "",
                    hostName = "",
                    longitude = location.longitude,
                    latitude = location.latitude,
                    bankId = 1L
                )
            )
            handleResult(result)
        }
    }

    fun setupInfoCredit(creditInfo: CreditosActivo, amountToPayment: String) {
        creditPayment = creditInfo
        this.amountToPayment = amountToPayment
    }

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success -> handleSuccess(result as CommonStatusServiceState.Success<SpeiDataResponse>)
            is CommonStatusServiceState.Error -> handleError(result)
            else -> {}
        }
    }

    override fun handleResult(state: CodeSecurityState) {
        when(state) {
            is CodeSecurityState.Loading -> _uiState.value = UIStates.Loading
            is CodeSecurityState.Success -> paymentCredit()
            is CodeSecurityState.Error -> _uiState.postValue(UIStates.Error(message = state.message))
        }
    }

    private fun handleSuccess(state: CommonStatusServiceState.Success<SpeiDataResponse>) {
        _uiState.postValue(UIStates.Success<SpeiDataResponse>(value = state.value as SpeiDataResponse))
    }

    private fun handleError(state: CommonStatusServiceState.Error) {
        _uiState.postValue(UIStates.Error(state.message))
    }

    fun resetUiState() {
        _uiState.value = UIStates.Init
    }

}