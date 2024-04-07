package asp.android.asppagos.ui.viewmodels.send_money

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.AMOUNT_TRANSACTION
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.CONCEPT_TRANSACTION
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.FAVORITE_ACCOUNT
import asp.android.asppagos.ui.fragments.send_money.SendMoneyDetailFragment.Companion.REFERENCE_TRANSACTION
import asp.android.asppagos.utils.toJson

class SendMoneyDetailViewModel : ViewModel() {

    private val _enabledButton =
        MutableLiveData<Boolean>().apply { value = false }
    val enabledButton: LiveData<Boolean> get() = _enabledButton

    private var amount: String = ""
    private var concept: String = ""
    private var reference: String = ""
    val pattern = Regex("([A-Za-z0-9 ]{1,40})")

    fun checkAmount(amount: String) {
        this.amount = amount
        checkContinueButton()
    }

    fun checkReference(reference: String) {
        this.reference = reference
        checkContinueButton()
    }

    fun checkConcept(concept: String) {
        this.concept = concept
        checkContinueButton()
    }

    private fun checkContinueButton() {
        _enabledButton.value = amount.isNotEmpty() && reference.isNotEmpty() && concept.isNotEmpty()
    }

    fun getTransactionBundle(userBeneficiary: FavoritosTransferencia?): Bundle {
        return bundleOf(
            Pair(AMOUNT_TRANSACTION, amount),
            Pair(CONCEPT_TRANSACTION, concept),
            Pair(REFERENCE_TRANSACTION, reference),
            Pair(FAVORITE_ACCOUNT, userBeneficiary.toJson())
        )
    }
}