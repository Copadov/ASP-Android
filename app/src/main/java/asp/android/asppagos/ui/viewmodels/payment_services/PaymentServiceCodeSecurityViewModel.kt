package asp.android.asppagos.ui.viewmodels.payment_services

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.payment_services.HeaderPaymentServiceRequest
import asp.android.asppagos.data.models.payment_services.MapPaymentServiceRequest
import asp.android.asppagos.data.models.payment_services.PaymentServiceDataResponse
import asp.android.asppagos.data.models.payment_services.PaymentServiceRequest
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.data.repositories.CheckUserPinRepository
import asp.android.asppagos.data.repositories.PaymentServiceRepository
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.ui.viewmodels.CodeSecurityBaseViewModel
import asp.android.asppagos.ui.viewmodels.CodeSecurityState
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toSpecificLengthFormat
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentServiceCodeSecurityViewModel(private val paymentServiceRepository: PaymentServiceRepository, private val pinRepository: CheckUserPinRepository) :
    CodeSecurityBaseViewModel(pinRepository) {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val _uiState: MutableLiveData<UIStates<PaymentServiceDataResponse>> =
        MutableLiveData<UIStates<PaymentServiceDataResponse>>().apply { value = UIStates.Init }
    val uiState: LiveData<UIStates<PaymentServiceDataResponse>> get() = _uiState

    private var serviceReference: String? = ""
    var serviceAmount: String? = ""
    var serviceInfo: ServiceDataResponse? = null

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

    fun setInfoPaymentService(
        serviceReference: String?,
        serviceAmount: String?,
        serviceInfo: ServiceDataResponse?
    ) {
        this.serviceReference = serviceReference
        this.serviceAmount = serviceAmount
        this.serviceInfo = serviceInfo
    }

    fun paymentService() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = paymentServiceRepository.paymentServices(
                PaymentServiceRequest(
                    accesoId = "324512",
                    header = HeaderPaymentServiceRequest(
                        idCanalAtencion = 2,
                        idClaseCanalAtencion = 0,
                        idComisionista = 0,
                        idEmpresa = 0,
                        idPuntoAtencion = 0,
                        idResponsabilidad = 0,
                        idSesion = 0,
                        idSucursal = 0,
                        idTransaccion = 0,
                        idUbicacion = 0,
                        idUsuario = 0,
                        ipHost = "9.9.9.9",
                        longitud = location.longitude,
                        latitud = location.latitude,
                        nameHost = "localhost",
                        usuarioClave = ""
                    ),
                    map = MapPaymentServiceRequest(
                        codBarra = serviceReference,
                        codigo = serviceInfo?.codigo,
                        cuenta = account.cuenta.cuenta,
                        monto = serviceAmount?.toDouble()?.toSpecificLengthFormat(12),
                        montoCs = serviceInfo?.comision?.toSpecificLengthFormat(4),
                        nemEmp = serviceInfo?.nememp,
                        numMed = serviceReference,
                        subEmp = serviceInfo?.subemp,
                    ),
                    proceso = "Pago de servicios",
                    tipo = "0"
                )
            )
            handleResult(result)
        }
    }

    private fun handleResult(result: CommonStatusServiceState<Any>) {
        when (result) {
            is CommonStatusServiceState.Success -> handleSuccess(result)
            is CommonStatusServiceState.Error -> handleError(result)
        }
    }

    override fun handleResult(state: CodeSecurityState) {
        when (state) {
            is CodeSecurityState.Loading -> _uiState.value = UIStates.Loading
            is CodeSecurityState.Success -> paymentService()
            is CodeSecurityState.Error -> _uiState.postValue(UIStates.Error(message = state.message))
        }
    }

    private fun handleSuccess(success: CommonStatusServiceState.Success<Any>) {
        _uiState.postValue(UIStates.Success<PaymentServiceDataResponse>(value = success.value as PaymentServiceDataResponse))
    }

    private fun handleError(error: CommonStatusServiceState.Error) {
        _uiState.postValue(UIStates.Error(message = error.message))
    }

    fun resetUiState() {
        _uiState.value = UIStates.Init
    }

}