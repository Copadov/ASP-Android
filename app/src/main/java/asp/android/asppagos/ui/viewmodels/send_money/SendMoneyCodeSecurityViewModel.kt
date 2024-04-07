package asp.android.asppagos.ui.viewmodels.send_money

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asp.android.aspandroidcore.utils.typeAccountId
import asp.android.asppagos.data.models.FavoritosTransferencia
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

class SendMoneyCodeSecurityViewModel(
    private val speiRepository: SpeiRepository,
    private val checkUserPinRepository: CheckUserPinRepository
) : CodeSecurityBaseViewModel(checkUserPinRepository) {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val _uiState = MutableLiveData<UIStates<SpeiDataResponse>>()
    val uiState: LiveData<UIStates<SpeiDataResponse>> get() = _uiState

    private var amountTransaction: String = ""
    private var conceptTransaction: String = ""
    private var referenceTransaction: String = ""
    private var favoriteAccount: FavoritosTransferencia? = null

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

    fun setupTransaction(
        amount: String,
        concept: String,
        reference: String,
        favoriteAccount: FavoritosTransferencia?
    ) {
        amountTransaction = amount
        conceptTransaction = concept
        referenceTransaction = reference
        this.favoriteAccount = favoriteAccount
    }


    fun sendSpeiTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = speiRepository.sendSpeiTransaction(
                CifDataRequest(
                    paymentConcept = conceptTransaction,
                    beneficiaryEmail = favoriteAccount?.correoBeneficiario,
                    beneficiaryAccount = favoriteAccount?.numeroCuentaBeneficiario,
                    payerAccount = account.cuenta.clabe,
                    createDate = System.currentTimeMillis(),
                    bornDate = DateDataRequest(
                        day = "26",
                        month = "12",
                        year = "1963"
                    ), //SE QUEDA ASI
                    beneficiaryInstitutionID = favoriteAccount?.idInstitucionBeneficiario, // TODO: REMOVE THIS HARDCODE LINE
                    payerInstitutionID = 90659,
                    beneficiaryTypeAccountID = favoriteAccount?.numeroCuentaBeneficiario.typeAccountId(), // regla del tipo de cuenta
                    payerTypeAccountID = 40, // CLABE
                    paymentTypeID = 1, // SE QUEDA COMO UNO
                    amount = amountTransaction.toDouble(),
                    beneficiaryName = favoriteAccount?.nombreBeneficiario,
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

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success -> {
                //Log.d("JHMM", "data transaction: ${result.data as SpeiDataResponse}")
                _uiState.postValue(UIStates.Success<SpeiDataResponse>(value = result.value as SpeiDataResponse))
            }

            is CommonStatusServiceState.Error -> _uiState.postValue(UIStates.Error(message = result.message))
            else -> {}
        }
    }

    fun restartState() {
        _uiState.value = UIStates.Init
    }

    override fun handleResult(state: CodeSecurityState) {
        when (state) {
            is CodeSecurityState.Loading -> _uiState.value = UIStates.Loading
            is CodeSecurityState.Success -> sendSpeiTransaction()
            is CodeSecurityState.Error -> _uiState.postValue(UIStates.Error(message = state.message))
        }
    }

    fun resetUiState() {
        _uiState.value = UIStates.Init
    }
}