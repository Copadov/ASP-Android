package asp.android.asppagos.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.aspandroidcore.utils.SingleLiveEvent
import asp.android.asppagos.data.models.*
import asp.android.asppagos.data.repositories.MainDashboardRepository
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.ui.activities.MainDashboardActivity
import asp.android.asppagos.utils.DEVICE_ID_UNIQUE_GUID
import asp.android.asppagos.utils.EncryptUtils.encryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.SingletonPassword
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.dividirCadena
import asp.android.asppagos.utils.encriptData
import asp.android.asppagos.utils.encriptDataLocal
import asp.android.asppagos.utils.encriptPassword
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainDashboardViewModel(
    private val mainRepository: MainDashboardRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    val errorMessage = SingleLiveEvent<String>()
    val infoMessage = SingleLiveEvent<String>()

    val movementsQueryResponseData = SingleLiveEvent<MovementsQueryResponseData>()
    val movementsQueryFilterResponseData = SingleLiveEvent<MovementsQueryResponseData>()
    val assignAccountResponseData = SingleLiveEvent<CodeResponseData>()
    val getBalanceResponseData = SingleLiveEvent<CodeResponseData>()
    val getBankListResponseData = SingleLiveEvent<CodeResponseData>()
    val getCVVResponseData = SingleLiveEvent<CodeResponseData>()
    val lockCardResponseData = SingleLiveEvent<CodeResponseData>()
    val requestReplacementResponseData = SingleLiveEvent<CodeResponseData>()
    val consultAccountResponseData = SingleLiveEvent<CodeResponseData>()
    val consultAccountPhysicResponseData = SingleLiveEvent<CodeResponseData>()
    val movementsFileResponseData = SingleLiveEvent<CodeResponseData>()
    val getAddressResponseData = SingleLiveEvent<CodeResponseData>()
    val cardAssignResponseData = SingleLiveEvent<CodeResponseData>()
    val isLoading = MutableLiveData<Boolean>()
    val fingerValidateResponse = SingleLiveEvent<CodeResponseData>()
    val validateAuthorizationCodeResponse = SingleLiveEvent<CodeResponseData>()
    val assignNipCodeResponseData = SingleLiveEvent<CodeResponseData>()

    /**
     * Live data for QR message read from NFC intent,
     * to be processed in [asp.android.asppagos.ui.fragments.codi.PagarCodiFragment]
     */
    private val _nfcMessage = SingleLiveEvent<String>()
    val nfcMessage: SingleLiveEvent<String> get() = _nfcMessage

    private val _enabledButtons: MutableLiveData<Boolean> = MutableLiveData()
    val enabledButtons: LiveData<Boolean> get() = _enabledButtons

    private var codeSecurity: String = ""
    private var codeSecurityConfirmation: String = ""

    fun codeSecurity(codeSecurity: String) {
        this.codeSecurity = codeSecurity
        checkEnabledButtons()
    }

    fun codeSecurityConfirmation(codeSecurityConfirmation: String) {
        this.codeSecurityConfirmation = codeSecurityConfirmation
        checkEnabledButtons()
    }

    /**
     * Saves the QR message read from NFC intent into [_nfcMessage].
     *
     * @param msg Message containing QR information
     */
    fun setNfcMessage(msg: String) {
        _nfcMessage.postValue(msg)
    }

    private fun checkEnabledButtons() {
        _enabledButtons.value = codeSecurity == codeSecurityConfirmation
    }

    private val _startDate = MutableLiveData<String>()
    val startDate: LiveData<String>
        get() = _startDate

    private val _finishDate = MutableLiveData<String>()
    val finishDate: LiveData<String>
        get() = _finishDate

    private val _account = MutableLiveData<String>()
    val account: LiveData<String>
        get() = _account

    private val _cardType = MutableLiveData<String>()
    val cardType: LiveData<String>
        get() = _cardType

    private val _accountName = MutableLiveData<String>()
    val accountName: LiveData<String>
        get() = _accountName

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double>
        get() = _balance

    private val _virtualCard = MutableLiveData<String>()
    val virtualCard: LiveData<String>
        get() = _virtualCard

    private val _physicCard = MutableLiveData<String>()
    val physicCard: LiveData<String>
        get() = _physicCard

    private val _cvvCard = MutableLiveData<String>()
    val cvvCard: LiveData<String>
        get() = _cvvCard

    private val _pinCode = MutableLiveData<String>()
    val pinCode: LiveData<String>
        get() = _pinCode

    private val _street = MutableLiveData<String>()
    val street: LiveData<String>
        get() = _street

    private val _numInt = MutableLiveData<String>()
    val numInt: LiveData<String>
        get() = _numInt

    private val _numExt = MutableLiveData<String>()
    val numExt: LiveData<String>
        get() = _numExt

    private val _cpCode = MutableLiveData<String>()
    val cpCode: LiveData<String>
        get() = _cpCode

    private val _colony = MutableLiveData<String>()
    val colony: LiveData<String>
        get() = _colony

    private val _city = MutableLiveData<String>()
    val city: LiveData<String>
        get() = _city


    fun setStartDate(startDate: String) {
        _startDate.value = startDate
    }

    fun setFinishDate(finishDate: String) {
        _finishDate.value = finishDate
    }

    fun setAccount(account: String) {
        _account.value = account
    }

    fun setCardType(cardType: String) {
        _cardType.value = cardType
    }

    fun setAccountName(accountName: String) {
        _accountName.value = accountName
    }

    fun setBalance(balance: Double) {
        _balance.value = balance
    }

    fun setVirtualCard(virtualCard: String) {
        _virtualCard.value = virtualCard
    }

    fun setPhysicCard(physicCard: String) {
        _physicCard.value = physicCard
    }

    fun setCVVCard(cvvCard: String) {
        _cvvCard.value = cvvCard
    }

    fun setPincode(pinCode: String) {
        _pinCode.value = pinCode
    }

    fun setStreet(street: String) {
        _street.value = street
    }

    fun setNumInt(numInt: String) {
        _numInt.value = numInt
    }

    fun setNumExt(numExt: String) {
        _numExt.value = numExt
    }

    fun setCpCode(cpCode: String) {
        _cpCode.value = cpCode
    }

    fun setColony(colony: String) {
        _colony.value = colony
    }

    fun setCity(city: String) {
        _city.value = city
    }

    fun validateAuthorizationCode() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.validateAuthorizationCode(
                        encriptData(
                            ValidateAuthorizationCodeRequestData(
                                servicioId = 2,
                                usuarioId = MainDashboardActivity.accountData.id,
                                codigo = encriptPassword(pinCode.value!!),
                                biometrico = false
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    validateAuthorizationCodeResponse
                        .postValue(
                            result.data
                        )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun validateFinger() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {

                    val dataEncripted = encriptDataLocal(
                        Prefs.get(DEVICE_ID_UNIQUE_GUID, ""),
                        MainDashboardActivity.accountData.cuenta.telefono,
                        encriptPassword(SingletonPassword.getSessionPassword())
                    )
                    Log.d("JHMM", "finger token: ${dataEncripted}")

                    Prefs.set(PROPERTY_FINGER_TOKEN, dataEncripted)

                    mainRepository.registerFinger(
                        encriptData(
                            RegisterFingerRequestData(
                                telefono = MainDashboardActivity.accountData.cuenta.telefono,
                                tipo = "H",
                                data = dataEncripted
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    fingerValidateResponse
                        .postValue(
                            result.data
                        )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun getBalance() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.getBalance(
                        encriptData(
                            GetBalanceRequestData(
                                cuenta = account.value!!,
                                idCanal = 1
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.data.isNotEmpty()) {
                        getBalanceResponseData.postValue(
                            result.data
                        )
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun getBankList() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.getBanksList()
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.data.isNotEmpty()) {
                        getBankListResponseData.postValue(
                            result.data
                        )
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun accountAssign() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.accountAssign(
                        encriptData(
                            accountAssignDataRequest(
                                header = Header(
                                    idCanalAtencion = "1",
                                    idClaseCanalAtencion = "0",
                                    idComisionista = "0",
                                    idEmpresa = "0",
                                    idPuntoAtencion = "0",
                                    idResponsabilidad = "0",
                                    idSesion = "0",
                                    idSucursal = "0",
                                    idTransaccion = "0",
                                    idUbicacion = "0",
                                    idUsuario = "1000",
                                    ipHost = "0.0.0.0",
                                    latitud = "51813",
                                    longitud = "746822",
                                    nameHost = "localHost",
                                    usuarioClave = "string"
                                ),
                                cuenta = account.value!!,
                                nombreEmbozar = accountName.value!!.dividirCadena(),
                                tipoTarjeta = cardType.value!!,
                                token = ""
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.data.isNotEmpty()) {
                        assignAccountResponseData.postValue(
                            result.data
                        )
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun lockCard() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardBlock(
                        encriptData(
                            lockCardDataRequest(
                                header = Header2(),
                                numeroTarjeta = virtualCard.value!!,
                                motivoBloqueo = "004"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    lockCardResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun lockPhysicCard() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardBlock(
                        encriptData(
                            lockCardDataRequest(
                                header = Header2(),
                                numeroTarjeta = physicCard.value!!,
                                motivoBloqueo = "004"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    lockCardResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun unlockCard() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardUnblock(
                        encriptData(
                            UnlockCardDataRequest(
                                header = Header(
                                    idCanalAtencion = "2",
                                    idClaseCanalAtencion = "0",
                                    idComisionista = "0",
                                    idEmpresa = "0",
                                    idPuntoAtencion = "0",
                                    idResponsabilidad = "0",
                                    idSucursal = "0",
                                    idTransaccion = "0",
                                    idUbicacion = "0",
                                    idUsuario = "100",
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost"
                                ),
                                numeroTarjeta = virtualCard.value!!,
                                motivoBloqueo = "004"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    lockCardResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun unlockPhysicCard() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardUnblock(
                        encriptData(
                            UnlockCardDataRequest(
                                header = Header(
                                    idCanalAtencion = "2",
                                    idClaseCanalAtencion = "0",
                                    idComisionista = "0",
                                    idEmpresa = "0",
                                    idPuntoAtencion = "0",
                                    idResponsabilidad = "0",
                                    idSucursal = "0",
                                    idTransaccion = "0",
                                    idUbicacion = "0",
                                    idUsuario = "100",
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost"
                                ),
                                numeroTarjeta = physicCard.value!!,
                                motivoBloqueo = "004"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    lockCardResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun cvvQuery() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cvvQuery(
                        encriptData(
                            CvvQueryDataRequest(
                                header = Header(
                                    idCanalAtencion = "2",
                                    idClaseCanalAtencion = "0",
                                    idComisionista = "0",
                                    idEmpresa = "0",
                                    idPuntoAtencion = "0",
                                    idResponsabilidad = "0",
                                    idSucursal = "0",
                                    idTransaccion = "0",
                                    idUbicacion = "0",
                                    idUsuario = "100",
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost"
                                ),
                                tarjeta = virtualCard.value!!.toLong(),
                                tipoMedioAcceso = "MED01",
                                medioAcceso = "Medio Acceso 1",
                                Idsolicitud = 1
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.data.isNotEmpty()) {
                        getCVVResponseData.postValue(
                            result.data
                        )
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun movementsQuery() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.movementsQuery(
                        encriptData(
                            CvvQueryMovementsDataRequest(
                                header = HeaderX(
                                    idEmpresa = 0,
                                    idResponsabilidad = 0,
                                    idClaseCanalAtencion = 0,
                                    idCanalAtencion = 2,
                                    idPuntoAtencion = 0,
                                    idUbicacion = 0,
                                    idSucursal = 0,
                                    idUsuario = 100,
                                    idComisionista = 0,
                                    idTransaccion = 0,
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost",
                                    numCuenta = "0620857854"
                                ),
                                medioAcceso = "",
                                fechaInicial = startDate.value!!,
                                maxMovimientos = "50",
                                tipoTarjeta = "",
                                ClaveTipoCuenta = "",
                                fechaFinal = finishDate.value!!,
                                tipoMedioAcceso = "MED01",
                                numeroTarjeta = MainDashboardActivity.accountData.cuenta.cuenta,
                                token = ""
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.code == 0) {
                        if (result.data.data.isNotEmpty()) {
                            movementsQueryResponseData.postValue(
                                Gson().fromJson<MovementsQueryResponseData>(
                                    decryptData(
                                        result.data.data
                                    )
                                )
                            )
                        }
                    } else if (result.data.code == -20) {
                        infoMessage.postValue(result.data.mensage)
                    } else {
                        errorMessage.postValue(result.data.mensage)
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun movementsQueryFilter() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.movementsQuery(
                        encriptData(
                            CvvQueryMovementsDataRequest(
                                header = HeaderX(
                                    idEmpresa = 0,
                                    idResponsabilidad = 0,
                                    idClaseCanalAtencion = 0,
                                    idCanalAtencion = 2,
                                    idPuntoAtencion = 0,
                                    idUbicacion = 0,
                                    idSucursal = 0,
                                    idUsuario = 100,
                                    idComisionista = 0,
                                    idTransaccion = 0,
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost",
                                    numCuenta = "0620857854"
                                ),
                                medioAcceso = "",
                                fechaInicial = startDate.value!!,
                                maxMovimientos = "50",
                                tipoTarjeta = "",
                                ClaveTipoCuenta = "",
                                fechaFinal = finishDate.value!!,
                                tipoMedioAcceso = "MED01",
                                numeroTarjeta = MainDashboardActivity.accountData.cuenta.cuenta,
                                token = ""
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    if (result.data.code == 0) {
                        if (result.data.data.isNotEmpty()) {
                            movementsQueryFilterResponseData.postValue(
                                Gson().fromJson<MovementsQueryResponseData>(
                                    decryptData(
                                        result.data.data
                                    )
                                )
                            )
                        }
                    } else if (result.data.code == -20) {
                        infoMessage.postValue(result.data.mensage)
                    } else {
                        errorMessage.postValue(result.data.mensage)
                    }
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun cardFund() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardWithdraw(
                        encriptData(
                            CardWithdrawDataRequest(
                                header = HeaderX(
                                    idSesion = 0,
                                    idEmpresa = 0,
                                    idResponsabilidad = 0,
                                    usuarioClave = "A28F5B852414F9402782F091E5EF8666",
                                    idUsuario = 100,
                                    idClaseCanalAtencion = 0,
                                    idCanalAtencion = 2,
                                    idPuntoAtencion = 0,
                                    idUbicacion = 0,
                                    idSucursal = 0,
                                    idComisionista = 0,
                                    idTransaccion = 0,
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost",
                                    longitud = 0,
                                    idBanco = "1",
                                    numCuenta = "0520033911"
                                ),
                                importe = "1.00",
                                referenciaNumerica = "1234569",
                                medioPago = "10",
                                observaciones = "",
                                numero_tarjeta = "9900032300143298"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {

                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun cardAssign() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardAssign(
                        encriptData(
                            CardReassignRequestData(
                                header = HeaderXX(
                                    idSesion = "0",
                                    idEmpresa = "0",
                                    idResponsabilidad = "0",
                                    usuarioClave = "A28F5B852414F9402782F091E5EF8666",
                                    idUsuario = "0",
                                    idClaseCanalAtencion = "0",
                                    idCanalAtencion = "0",
                                    idPuntoAtencion = "0",
                                    idUbicacion = "0",
                                    idSucursal = "0",
                                    idComisionista = "0",
                                    idTransaccion = "0",
                                    ipHost = "172.17.5.9",
                                    nameHost = "Ali",
                                    longitud = "0",
                                    latitud = "0",
                                    numCuenta = "0",
                                    idBanco = "0"
                                ),
                                IDSolicitud = "1",
                                ClaveEmpresa = "99000047",
                                numeroTarjeta = virtualCard.value!!,
                                numeroTarjetaExtra = "",
                                token = "",
                                calle = street.value!!,
                                calle2 = "",
                                num_interior = numInt.value!!,
                                num_exterior = numExt.value!!,
                                referencia = "",
                                cp = cpCode.value!!,
                                colonia = colony.value!!,
                                municipio = "",
                                estado = "",
                                tipo_entrega = "domicilio/otro",
                                nti = "1"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    cardAssignResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun assignNip() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.assignPIN(
                        encriptData(
                            AssingNipDataRequest(
                                header = HeaderX(
                                    idSesion = 0,
                                    idEmpresa = 0,
                                    idResponsabilidad = 0,
                                    usuarioClave = "A28F5B852414F9402782F091E5EF8666",
                                    idUsuario = 100,
                                    idClaseCanalAtencion = 0,
                                    idCanalAtencion = 2,
                                    idPuntoAtencion = 0,
                                    idUbicacion = 0,
                                    idSucursal = 0,
                                    idComisionista = 0,
                                    idTransaccion = 0,
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost",
                                    longitud = 0,
                                    idBanco = "1",
                                    numCuenta = "0520033911"
                                ),
                                nip = codeSecurity,
                                numeroTarjeta = physicCard.value!!
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    assignNipCodeResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun getAddress() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.getAddress(
                        encriptData(
                            GetAddressRequestData(
                                header = HeaderXX(
                                    idBanco = "1",
                                    idCanalAtencion = "0",
                                    idClaseCanalAtencion = "0",
                                    idComisionista = "0",
                                    idEmpresa = "0",
                                    idPuntoAtencion = "0",
                                    idResponsabilidad = "0",
                                    idSesion = "0",
                                    idSucursal = "0",
                                    idTransaccion = "0",
                                    idUbicacion = "0",
                                    idUsuario = "1000",
                                    ipHost = "9.9.9.9",
                                    longitud = "0",
                                    latitud = "0",
                                    nameHost = "localhost",
                                    numCuenta = MainDashboardActivity.accountData.cuenta.cuenta,
                                    usuarioClave = ""

                                ),
                                solicitante_id = MainDashboardActivity.accountData.id
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    getAddressResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun requestReplacement() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.requestReplacement(
                        encriptData(
                            ReplacementDataRequest(
                                header = HeaderX(
                                    idSesion = 0,
                                    idEmpresa = 0,
                                    idResponsabilidad = 0,
                                    usuarioClave = "A28F5B852414F9402782F091E5EF8666",
                                    idUsuario = 100,
                                    idClaseCanalAtencion = 0,
                                    idCanalAtencion = 2,
                                    idPuntoAtencion = 0,
                                    idUbicacion = 0,
                                    idSucursal = 0,
                                    idComisionista = 0,
                                    idTransaccion = 0,
                                    ipHost = "127.0.0.1",
                                    nameHost = "localHost",
                                    longitud = 0,
                                    idBanco = "1",
                                    numCuenta = "0520033911"
                                ),
                                IDSolicitud = "1",
                                MedioAcceso = "Medio Acceso 1",
                                nombreEmbozado = accountName.value!!.dividirCadena(),
                                razonReposicion = "1",
                                TipoMedioAcceso = "MED01",
                                numeroTarjeta = virtualCard.value!!
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    requestReplacementResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun consultPhysicAccount() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.consultAccount(
                        encriptData(
                            ConsultantAccountRequestData(
                                IDSolicitud = "1",
                                header = HeaderXX(
                                    idSesion = "1",
                                    idEmpresa = "0",
                                    idResponsabilidad = "0",
                                    usuarioClave = "string",
                                    idUsuario = "100",
                                    idClaseCanalAtencion = "0",
                                    idCanalAtencion = "2",
                                    idPuntoAtencion = "0",
                                    idUbicacion = "0",
                                    idSucursal = "0",
                                    idComisionista = "0",
                                    idTransaccion = "0",
                                    longitud = "0",
                                    ipHost = "0.0.0.0",
                                    nameHost = "localHost",
                                    idBanco = "1",
                                    numCuenta = MainDashboardActivity.accountData.cuenta.cuenta
                                ),
                                medioAcceso = "Medio Acceso 1",
                                tipoTarjeta = "F",
                                tipoMedioAcceso = "MED01",
                                numeroTarjeta = physicCard.value!!
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    consultAccountPhysicResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun consultAccount() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.consultAccount(
                        encriptData(
                            ConsultantAccountRequestData(
                                IDSolicitud = "1",
                                header = HeaderXX(
                                    idSesion = "1",
                                    idEmpresa = "0",
                                    idResponsabilidad = "0",
                                    usuarioClave = "string",
                                    idUsuario = "100",
                                    idClaseCanalAtencion = "0",
                                    idCanalAtencion = "2",
                                    idPuntoAtencion = "0",
                                    idUbicacion = "0",
                                    idSucursal = "0",
                                    idComisionista = "0",
                                    idTransaccion = "0",
                                    longitud = "0",
                                    ipHost = "0.0.0.0",
                                    nameHost = "localHost",
                                    idBanco = "1",
                                    numCuenta = MainDashboardActivity.accountData.cuenta.cuenta
                                ),
                                medioAcceso = "Medio Acceso 1",
                                tipoTarjeta = "V",
                                tipoMedioAcceso = "MED01",
                                numeroTarjeta = virtualCard.value!!
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    consultAccountResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun requestMovementsFile(fechaInicial: Long, fechaFinal: Long) {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.generateReportStatusMovementsV2(
                        encriptData(
                            GetMovementsFileRequestData(
                                cuenta = account.value!!,
                                idCanal = 1,
                                data = (
                                        GetMovementsFileDataRequestData(
                                            cuenta = account.value!!,
                                            fechaFinal = fechaFinal+43200,
                                            fechaInicial = fechaInicial+43201
                                        )
                                        ).encryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass()
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    movementsFileResponseData.postValue(
                        result.data
                    )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun cardReassign() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    mainRepository.cardReassign(
                        encriptData(
                            CardReassignRequestData(
                                header = HeaderXX(
                                    idSesion = "0",
                                    idEmpresa = "0",
                                    idResponsabilidad = "0",
                                    usuarioClave = "A28F5B852414F9402782F091E5EF8666",
                                    idUsuario = "0",
                                    idClaseCanalAtencion = "0",
                                    idCanalAtencion = "0",
                                    idPuntoAtencion = "0",
                                    idUbicacion = "0",
                                    idSucursal = "0",
                                    idComisionista = "0",
                                    idTransaccion = "0",
                                    ipHost = "172.17.5.9",
                                    nameHost = "Ali",
                                    longitud = "0",
                                    latitud = "0",
                                    numCuenta = "0",
                                    idBanco = "0"
                                ),
                                IDSolicitud = "1",
                                ClaveEmpresa = "99000047",
                                numeroTarjeta = virtualCard.value!!,
                                numeroTarjetaExtra = "",
                                token = "",
                                calle = "Emiliano Zapata",
                                calle2 = "Los Ángeles",
                                num_interior = "51",
                                num_exterior = "51",
                                referencia = "porton azul",
                                cp = "38537",
                                colonia = "La Cuevita",
                                municipio = "Apaseo el Alto",
                                estado = "Guanajuato",
                                tipo_entrega = "domicilio/otro",
                                nti = "1"
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {

                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }
}