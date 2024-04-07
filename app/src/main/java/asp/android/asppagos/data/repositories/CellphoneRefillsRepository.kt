package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.CellphoneRefillAPI
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillHeaderRequest
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillRequest
import asp.android.asppagos.data.models.cellphone_refill.CellphoneRefillServiceResponse
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson

interface CellphoneRefillsRepository {
    suspend fun getCellphoneRefills(): CommonStatusServiceState<Any>
}

class CellphoneRefillsRepositoryImpl(private val cellphoneRefillAPI: CellphoneRefillAPI) :
    CellphoneRefillsRepository {

    override suspend fun getCellphoneRefills(): CommonStatusServiceState<Any> {

        val request = CellphoneRefillRequest(header = CellphoneRefillHeaderRequest())

        //Log.d("JHMM", "refill cellphone request: ${request.toJson()}")

        val requestEncrypted = request.encryptByGeneralKey()

        //Log.d("JHMM", "refill cellphone request encrypted: ${requestEncrypted}")

        val response = cellphoneRefillAPI.getCellphonesRefills(requestEncrypted).await()

        //Log.d("JHMM", "refill cellphone result: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

        //Log.d("JHMM", "refill cellphone result: ${responseDecrypted}")

        val dataDecrypted = responseDecrypted?.data?.decryptByGeneralKey<MutableList<CellphoneRefillServiceResponse>>()

        //Log.d("JHMM", "refill cellphone data result: ${dataDecrypted}")

        // Group from company
        val companyMap = mutableMapOf<String, MutableList<CellphoneRefillServiceResponse>>()

        dataDecrypted?.forEach { planCompany ->

            if (companyMap.containsKey(planCompany.service).not()) {
                companyMap[planCompany.service ?: "none"] = mutableListOf(planCompany)
            } else {
                companyMap[planCompany.service]?.add(planCompany)
            }
        }

        return try {
            getStatusServiceState<MutableList<CellphoneRefillServiceResponse>>(
                statusCode = responseDecrypted?.code,
                dataToWork = companyMap,
                message = responseDecrypted?.message
            )
        } catch(exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }
}