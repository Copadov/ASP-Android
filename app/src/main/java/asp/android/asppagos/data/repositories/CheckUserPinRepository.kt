package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.PinAPI
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.PinRequest
import asp.android.asppagos.data.models.PinRequestTypeTransaction
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByKeyPass
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_FINGER_TOKEN
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson

interface CheckUserPinRepository {
    suspend fun checkUserPin(
        userPin: String,
        pinRequestTypeTransaction: PinRequestTypeTransaction,
        isBiometric: Boolean
    ): CommonStatusServiceState<Any>
}

class CheckUserPinRepositoryImpl(private val pinAPI: PinAPI) : CheckUserPinRepository {


    override suspend fun checkUserPin(
        userPin: String,
        pinRequestTypeTransaction: PinRequestTypeTransaction,
        isBiometric: Boolean
    ): CommonStatusServiceState<Any> {

        val account =
            Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

        val passwordEncrypted = Prefs.get<String>(key = PROPERTY_FINGER_TOKEN)
        //Log.d("JHMM", "password: ${passwordEncrypted}")

        val checkUserBiometricOrPin = if (isBiometric) passwordEncrypted else userPin.encryptByKeyPass()

        val request = PinRequest(
            biometrico = isBiometric,
            codigo = checkUserBiometricOrPin,
            servicioId = pinRequestTypeTransaction.id,
            usuarioId = account.id
        )

        //Log.d("JHMM", "request: ${request.toJson()}")

        val requestEncrypted = request.encryptByGeneralKey()

        //Log.d("JHMM", "requestEncrypted: ${requestEncrypted.toJson()}")

        val response = pinAPI.checkUserPin(requestEncrypted).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

        //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

        return try {
            getStatusServiceState<Any>(
                statusCode = responseDecrypted?.code,
                dataToWork = null,
                message = responseDecrypted?.message
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }

    }

}