package asp.android.asppagos.ui.viewmodels.configurations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UpdatePersonalCodeViewModel : ViewModel() {

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

    private fun checkEnabledButtons() {
        _enabledButtons.value = codeSecurity == codeSecurityConfirmation
    }

}