package asp.android.asppagos.ui.viewmodels.credit_payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.asppagos.data.models.CreditosActivo
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.ui.states.UIStates
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson

class CreditPaymentMainViewModel : ViewModel() {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val _uiState = MutableLiveData<UIStates<MutableList<CreditosActivo>>>()
    val uiState: LiveData<UIStates<MutableList<CreditosActivo>>> get() = _uiState

    init {
        getCreditActive()
    }

    private fun getCreditActive() {
        _uiState.value = UIStates.Loading
        _uiState.value = UIStates.Success(value = account.creditosActivos.toMutableList())
    }

}