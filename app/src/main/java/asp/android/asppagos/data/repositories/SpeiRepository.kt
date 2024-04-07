package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.SpeiAPI
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CifDataRequest
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.HeaderData
import asp.android.asppagos.data.models.send_money.SimpleSpeiRequest
import asp.android.asppagos.data.models.send_money.SpeiDataResponse
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson

interface SpeiRepository {
    suspend fun sendSpeiTransaction(operationInfo: CifDataRequest, sessionInfo: HeaderData): CommonStatusServiceState<Any>
}

class SpeiRepositoryImpl(private val speiAPI: SpeiAPI) : SpeiRepository {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    override suspend fun sendSpeiTransaction(operationInfo: CifDataRequest, sessionInfo: HeaderData): CommonStatusServiceState<Any> {

        //Log.d("JHMM", "transaction spei info in repo: $operationInfo")

        //Log.d("JHMM", "request: ${operationInfo.toJson()}")

        // Encrypt cif data
        val cifDataEncrypt = operationInfo.encryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass()
        //Log.d("JHMM", "cifDataEncrypt: ${cifDataEncrypt.toJson()}")

        // Request encrypt
        val requestEncrypted = SimpleSpeiRequest(
            savingAccount = account.cuenta.cuenta,
            dataCif = cifDataEncrypt,
            idChannel = 1,
            header = sessionInfo
        ).encryptByGeneralKey()
        //Log.d("JHMM", "requestEncrypted: ${requestEncrypted.toJson()}")

        // Send request
        val response = speiAPI.sendSpeiTransaction(requestEncrypted).await()
        //Log.d("JHMM", "response: ${response}")

        // Decrypt response
        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()
        //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

        // Decrypt response data
        return try {
            val dataDecrypted = if (responseDecrypted?.code != 100) {
                responseDecrypted?.data?.decryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass<SpeiDataResponse>()
            } else {
                responseDecrypted.data?.decryptByGeneralKey<SpeiDataResponse>()
            }
            //Log.d("JHMM", "dataDecrypted: ${dataDecrypted}")
            getStatusServiceState<SpeiDataResponse>(statusCode = responseDecrypted?.code, dataToWork = dataDecrypted, message = responseDecrypted?.message)
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}