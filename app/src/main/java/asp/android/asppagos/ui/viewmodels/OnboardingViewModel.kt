package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.aspandroidcore.utils.SingleLiveEvent
import asp.android.asppagos.data.models.*
import asp.android.asppagos.data.repositories.OCRRepository
import asp.android.asppagos.data.repositories.OnboardingRepository
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class OnboardingViewModel(
    private val ocrRepository: OCRRepository,
    private val onboardingRepository: OnboardingRepository
) : ViewModel(),
    CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    val isLoading = SingleLiveEvent<Boolean>()

    val successResponse = SingleLiveEvent<CodeResponseData>()
    val successSendCodeResponse = SingleLiveEvent<CodeResponseData>()
    val beneficiaryResponse = SingleLiveEvent<CodeResponseData>()
    val successVerifiedResponse = SingleLiveEvent<CodeResponseData>()
    val fingerValidateResponse = SingleLiveEvent<CodeResponseData>()
    val cpValidateResponse = SingleLiveEvent<List<CPResponseData>>()
    val errorMessage = SingleLiveEvent<String>()
    val registerAccountResponseData = SingleLiveEvent<CodeResponseData>()
    val registerImageINEResponseData = SingleLiveEvent<CodeResponseData>()

    var rear = ""
    var front = ""

    private val _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

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

    private val _cpSelected = MutableLiveData<CPResponseData>()
    val cpSelected: LiveData<CPResponseData>
        get() = _cpSelected

    private val _city = MutableLiveData<String>()
    val city: LiveData<String>
        get() = _city

    private val _rfc = MutableLiveData<String>("")
    val rfc: LiveData<String>
        get() = _rfc

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String>
        get() = _phone

    private val _pass = MutableLiveData<String>()
    val pass: LiveData<String>
        get() = _pass

    private val _geolocalizacion = MutableLiveData<Geolocalizacion>()
    val geolocalizacion: LiveData<Geolocalizacion>
        get() = _geolocalizacion

    private val _validatePin = MutableLiveData<String>()
    val validatePin: LiveData<String>
        get() = _validatePin

    private val _nameBeneficiary = MutableLiveData<String>()
    val nameBeneficiary: LiveData<String>
        get() = _nameBeneficiary

    private val _lastNameBeneficiary = MutableLiveData<String>()
    val lastNameBeneficiary: LiveData<String>
        get() = _lastNameBeneficiary

    private val _surNameBeneficiary = MutableLiveData<String>()
    val surNameBeneficiary: LiveData<String>
        get() = _surNameBeneficiary

    private val _similBeneficiary = MutableLiveData<Int>()
    val similBeneficiary: LiveData<Int>
        get() = _similBeneficiary

    private val _accountNumber = MutableLiveData<String>()
    val accountNumber: LiveData<String>
        get() = _accountNumber

    private val _curpBeneficiary = MutableLiveData<String>()
    val curpBeneficiary: LiveData<String>
        get() = _curpBeneficiary

    fun setCPResponse(name: CPResponseData) {
        _cpSelected.value = name
    }

    fun setNameBeneficiary(name: String) {
        _nameBeneficiary.value = name
    }

    fun setLastNameBeneficiary(lastName: String) {
        _lastNameBeneficiary.value = lastName
    }

    fun setSurNameBeneficiary(surName: String) {
        _surNameBeneficiary.value = surName
    }

    fun setSimilBeneficiary(simil: Int) {
        _similBeneficiary.value = simil
    }

    fun setCurpBeneficiary(curpBeneciary: String) {
        _curpBeneficiary.value = curpBeneciary
    }

    fun setToken(token: String) {
        _token.value = token
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

    fun setRFC(rfc: String) {
        _rfc.value = rfc
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPhone(phone: String) {
        _phone.value = phone
    }

    fun setPass(pass: String) {
        _pass.value = pass
    }

    fun setAccountNumber(account: String) {
        _accountNumber.value = account
    }

    fun setGeolocalizacion(geolocalizacion: Geolocalizacion) {
        _geolocalizacion.value = geolocalizacion
    }

    fun setValidatePin(validatePin: String) {
        _validatePin.value = validatePin
    }

    private val _ocrDataResponse = SingleLiveEvent<OCRResponseData>()
    val ocrDataResponse: SingleLiveEvent<OCRResponseData>
        get() = _ocrDataResponse

    fun getOCRData(ocrRequestData: OCRRequestData) {
        launch {
            when (val result =
                withContext(Dispatchers.IO) { ocrRepository.getOCRData(ocrRequestData) }) {
                is UseCaseResult.Success -> _ocrDataResponse.postValue(result.data)
                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun sendCode() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                onboardingRepository.sendCode(
                    encriptData(SendCodeRequestData(phone.value!!))
                )
            }) {
                is UseCaseResult.Success -> {
                    successSendCodeResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun beneficiaryRegister() {
        launch {
            when (val result = withContext(Dispatchers.IO) {
                onboardingRepository.registerBeneficiary(
                    encriptData(
                        RegisterBeneficiaryRequestData(
                            header = HeaderXXX(),
                            cuenta = accountNumber.value!!,
                            beneficiarios = listOf(
                                Beneficiario(
                                    nombre = nameBeneficiary.value!!,
                                    parentesco = similBeneficiary.value!!,
                                    porcentaje = 100,
                                    primer_apellido = lastNameBeneficiary.value!!,
                                    segundo_apellido = surNameBeneficiary.value!!
                                )
                            )


                        )
                    )
                )
            }) {
                is UseCaseResult.Success -> {
                    beneficiaryResponse
                        .postValue(
                            result.data
                        )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun validatePhone() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    onboardingRepository.validatePhone(
                        encriptData(
                            CodeValidateRequestData(
                                phone.value!!, token.value!!,
                                1,
                                ocrDataResponse.value!!.curp
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun validateEmail(action: String) {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    onboardingRepository.validateEmail(
                        encriptData(
                            EmailValidateRequest(
                                email.value!!,
                                "98R3H389IOD389YN83D84D89K8YW84L9",
                                rfc.value!!,
                                ocrDataResponse.value!!.curp,
                                action,
                                nombre = ocrDataResponse.value!!.nombres
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    successResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun verifiedPhone() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    onboardingRepository.verifiedPhone(
                        encriptData(SendCodeRequestData(phone.value!!))
                    )
                }) {
                is UseCaseResult.Success -> {
                    successVerifiedResponse.postValue(result.data)
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun validateCP() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    onboardingRepository.validateCP(
                        encriptData(ValidateCPRequestData(cpCode.value!!))
                    )
                }) {
                is UseCaseResult.Success -> {

                    var response = result.data

                    if (response.codigo != 1) {
                        cpValidateResponse
                            .postValue(
                                Gson()
                                    .fromJson<List<CPResponseData>>(result.data.data)
                            )
                    } else {
                        errorMessage.postValue(response.mensaje)
                    }
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
                        phone.value!!,
                        encriptPassword(pass.value!!)
                    )

                    Prefs.set(PROPERTY_FINGER_TOKEN, dataEncripted)
                    Prefs.set(PROPERTY_PASSWORD_REGISTER, pass.value!!)

                    onboardingRepository.registerFinger(
                        encriptData(
                            RegisterFingerRequestData(
                                telefono = phone.value!!,
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

    fun registerImageIneSimpleAccount() {
        launch {
            when (val result =
                withContext(Dispatchers.IO) {
                    onboardingRepository.registerImageSimpleAccount(
                        encriptData(
                            RegisterImageSimpleAccountRequestData(
                                numeroCuenta = accountNumber.value!!,
                                validacionOcrReq = Gson().toJson(
                                    IneOCRRequestData(
                                        id = front,
                                        idReverso = rear
                                    )
                                )
                            )
                        )
                    )
                }) {
                is UseCaseResult.Success -> {
                    registerImageINEResponseData
                        .postValue(
                            result.data
                        )
                }

                is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
            }
        }
    }

    fun registerSimpleAccount() {
        var domicilio = street.value!!
        if (numExt.value?.isNotEmpty() == true) domicilio +=  " NUM EXT: ${numExt.value!!}"
        if (numInt.value?.isNotEmpty() == true) domicilio +=  " NUM INT: ${numInt.value!!}"

        try {
            launch {
                when (val result =
                    withContext(Dispatchers.IO) {
                        onboardingRepository.registerSimpleAccount(
                            encriptData(
                                RegisterAccountRequestData(
                                    "1",
                                    ocrDataResponse.value?.segundoApellido ?: "",
                                    ocrDataResponse.value?.primerApellido ?: "",
                                    phone.value!!,
                                    encriptPassword(validatePin.value!!),
                                    cpCode.value!!,
                                    "",
                                    0,
                                    cpSelected.value!!.colonia,
                                    ocrDataResponse.value?.curp ?: "",
                                    domicilio,
                                    email.value!!,
                                    geolocalizacion.value!!,
                                    //"",
                                    Prefs.get(DEVICE_ID_UNIQUE_GUID, ""),
                                    Header(
                                        "2",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        "0",
                                        idUsuario = "0",
                                        ipHost = "",
                                        latitud = "0",
                                        longitud = "0",
                                        nameHost = "",
                                        numCuenta = "",
                                        usuarioClave = ""
                                    ),
                                    true,
                                    ocrDataResponse.value?.nombres ?: "",
                                    rfc.value!!,
                                    POSITIVE_RESPONSE_REGISTER,
                                    POSITIVE_RESPONSE_REGISTER,
                                    encriptPassword(pass.value!!)
                                )
                            )
                        )
                    }) {
                    is UseCaseResult.Success -> {
                        registerAccountResponseData.postValue(result.data)
                    }

                    is UseCaseResult.Error -> errorMessage.postValue(result.exception.message)
                }
            }
        } catch (ex: Exception) {
            errorMessage.postValue(ex.message)
        }
    }
}