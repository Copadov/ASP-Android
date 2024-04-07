package asp.android.asppagos.ui.viewmodels.send_money

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.aspandroidcore.utils.typeAccountId
import asp.android.asppagos.data.models.Catinsti
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.SendMoneyDataRequest
import asp.android.asppagos.data.repositories.FavoriteRepository
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SendMoneyRegisterAccountViewModel(private val sendMoneyRepository: FavoriteRepository) :
    ViewModel(), CoroutineScope {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private var accountBeneficiary: String = ""
    private var bankBeneficiary: String = ""
    private var nameBeneficiary: String = ""
    private var lastNameBeneficiary: String = ""
    private var secondLastNameBeneficiary: String = ""
    private var aliasBeneficiary: String = ""
    private var businessName: String = ""
    val pattern = Regex("([A-Za-z0-9 ]{1,100})")
    private var isBusinessAccount = false

    // Change UI Model
    private val _uiState = MutableLiveData<UIStates<String>>()
    val uiState: LiveData<UIStates<String>> get() = _uiState

    private val _bankList = MutableLiveData<MutableList<String>>()
    val bankList: LiveData<MutableList<String>> get() = _bankList
    val cacheBankList: MutableList<Catinsti> = mutableListOf()

    private val _bankFilteredByClabe = MutableLiveData<String>()
    val bankFilteredByClabe: LiveData<String> get() = _bankFilteredByClabe

    private val _buttonContinue = MutableLiveData<Boolean>()
    val buttonContinue: LiveData<Boolean> get() = _buttonContinue

    init {
        setupBankList()
    }

    fun accountBeneficiary(account: String) {
        accountBeneficiary = account
        getBankByClabe(account)
        enabledContinueButton()
    }

    private fun getBankByClabe(clabe: String) {
        if (clabe.length == 18) {
            val clabeFirstDigits = clabe.substring(IntRange(0, 2))
            val bankFilter = cacheBankList.firstOrNull { bank ->
                val claveBankString = bank.clave_institucion.toString()
                val claveBankStringLength = claveBankString.length
                val claveBankLastDigits = claveBankString.substring(
                    IntRange(
                        claveBankStringLength - 3,
                        claveBankStringLength - 1
                    )
                )
                claveBankLastDigits == clabeFirstDigits
            }
            bankFilter?.let {
                _bankFilteredByClabe.value = it.nombreCorto
            }
        }
    }

    fun bankBeneficiary(bank: String) {
        bankBeneficiary = bank
        enabledContinueButton()
    }

    fun nameBeneficiary(name: String) {
        nameBeneficiary = name
        enabledContinueButton()
    }

    fun lastNameBeneficiary(lastName: String) {
        lastNameBeneficiary = lastName
        enabledContinueButton()
    }

    fun secondLastBeneficiary(secondLastName: String) {
        secondLastNameBeneficiary = secondLastName
        enabledContinueButton()
    }

    fun businessName(businessName: String) {
        this.businessName = businessName
        enabledContinueButton()
    }

    fun aliasBeneficiary(alias: String) {
        aliasBeneficiary = alias
    }

    private fun enabledContinueButton() {
        _buttonContinue.value = if (isBusinessAccount.not()) {
            accountBeneficiary.isNotEmpty() &&
                    checkBeneficiaryAccount(accountBeneficiary) &&
                    bankBeneficiary.isNotEmpty() &&
                    nameBeneficiary.isNotEmpty() &&
                    nameBeneficiary.matches(pattern) &&
                    lastNameBeneficiary.isNotEmpty() &&
                    lastNameBeneficiary.matches(pattern) &&
                    (secondLastNameBeneficiary.isBlank() || (secondLastNameBeneficiary.isNotBlank() && secondLastNameBeneficiary.matches(pattern)))
        } else {
            accountBeneficiary.isNotEmpty() &&
                    checkBeneficiaryAccount(accountBeneficiary) &&
                    bankBeneficiary.isNotEmpty() &&
                    businessName.isNotEmpty() &&
                    businessName.matches(pattern)
        }
    }

    private fun checkBeneficiaryAccount(accountBeneficiary: String): Boolean {
        return (accountBeneficiary.length < 10 || accountBeneficiary.length in 11..15 || accountBeneficiary.length == 17).not()
    }

    // TODO: REMOVE THIS HARDCODE
    fun registerAccount() {
        _uiState.value = UIStates.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val response = sendMoneyRepository.addToFavoriteAccount(
                getSendMoneyDataRequest()
            )
            handleResult(response)
        }
    }

    fun getSendMoneyDataRequest(): SendMoneyDataRequest {
        val selectedBank = account.listCatinsti.first { it.nombreCorto == bankBeneficiary }
        return SendMoneyDataRequest(
            codiNumberAccount = account.cuenta.cuenta,
            name = getBeneficiaryName(),
            numberAccount = accountBeneficiary,
            typeAccount = accountBeneficiary.typeAccountId(),
            idBank = selectedBank.clave_institucion,
            bankName = selectedBank.nombreCorto,
            email = ""
        )
    }

    private fun getBeneficiaryName(): String {
        return "$nameBeneficiary $lastNameBeneficiary $secondLastNameBeneficiary".takeIf {
            nameBeneficiary.isNotBlank() && lastNameBeneficiary.isNotBlank() && secondLastNameBeneficiary.isNotBlank()
        } ?: "$nameBeneficiary $lastNameBeneficiary".takeIf {
            nameBeneficiary.isNotBlank() && lastNameBeneficiary.isNotBlank() && secondLastNameBeneficiary.isBlank()
        } ?: businessName.takeIf { businessName.isNotBlank() } ?: "NONE"
    }

    private fun handleResult(commonState: CommonStatusServiceState<Any>) {
        when (commonState) {
            is CommonStatusServiceState.Success -> _uiState.postValue(UIStates.Success(null))
            is CommonStatusServiceState.Error -> _uiState.postValue(UIStates.Error(message = commonState.message))
            else -> {}
        }
    }

    private fun setupBankList() {
        cacheBankList.clear()
        cacheBankList.addAll(account.listCatinsti.toMutableList())
        _bankList.value =
            cacheBankList.map { it.nombreCorto }.filter { it.isNotEmpty() }.toMutableList()
    }

    fun restartState() {
        _uiState.value = UIStates.Init
    }

    fun setupIsBusiness(isBusiness: Boolean) {
        isBusinessAccount = isBusiness
    }

    fun getIsBusiness() = isBusinessAccount

}