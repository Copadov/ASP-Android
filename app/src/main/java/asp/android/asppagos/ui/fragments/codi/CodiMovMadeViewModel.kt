package asp.android.asppagos.ui.fragments.codi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asp.android.asppagos.data.interfaces.CodiAspAPI
import asp.android.asppagos.data.models.codi.CodiMovResponse
import asp.android.asppagos.data.models.codi.TransactionsRequest
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.CodiAspRepository
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CodiMovMadeViewModel(private val codiAspAPI: CodiAspRepository, private val  aspTrackingRepository: AspTrackingRepository): ViewModel() {

    private val _codiMovResponseObservable: MutableLiveData<MutableList<CodiMovResponse>> = MutableLiveData()
    val codiMovResponseObservable: LiveData<MutableList<CodiMovResponse>> get() = _codiMovResponseObservable

    fun getHistoryMovements(request: TransactionsRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            var webService = ""
            val result = codiAspAPI.getTransactionsCodiDeferred(request.encryptByGeneralKey()){
                webService = it
            }.await()
            val resultDecrypted = result.decryptByGeneralKey<CommonServiceResponse>()
            if (resultDecrypted?.code == 0) {
                val r = resultDecrypted.data?.decryptByGeneralKey<List<CodiMovResponse>>()
                val filterLs = r?.filter { mv -> !mv.status?.equals("pendiente", true)!! }
                filterLs?.let {
                    _codiMovResponseObservable.postValue(it.toMutableList())
                }
                aspTrackingRepository.consume(
                    webService = webService,
                    typeResponse = AspTrackingRepositoryImpl.ConsumeServiceTypeResponse.ERROR,
                    response = result
                )
            }
        }
    }

}