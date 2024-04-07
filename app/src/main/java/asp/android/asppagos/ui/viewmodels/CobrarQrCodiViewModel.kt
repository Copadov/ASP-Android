package asp.android.asppagos.ui.viewmodels

import androidx.lifecycle.ViewModel

class CobrarQrCodiViewModel : ViewModel() {

    private  var _amount: Double? = 0.0;
    private var _concept: String = "";
    private var _reference:String = "";
    private var _account:String = ""
    private var _accountNumber: String = ""


    val amount get() = _amount!!;
    val concept get() = _concept!!;
    val reference get() = _reference!!
    val account get() = _account!!
    val accountNumber get() = _accountNumber!!


    fun setAmount(amount: Double?){
        this._amount = amount;
     }
    fun setConcept(concept:String){
        this._concept = concept;
    }
    fun setReference(reference:String){
        this._reference = reference;
    }

    fun setAccount(account: String){
        this._account=account
    }
    fun setAccountNumber(accountNumber: String){
        this._accountNumber=accountNumber
    }


}