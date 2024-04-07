package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.OnboardingAPI
import asp.android.asppagos.data.interfaces.PinAPI
import asp.android.asppagos.data.interfaces.UpdateCodeApi
import asp.android.asppagos.data.models.CodeValidateRequestData
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.SendCodeRequestData
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.data.models.update_pin.UpdateCodeSecurityRequest
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByKeyPass
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_PHONE_USER_LOGGED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson

interface UpdateProfileRepository {
    suspend fun sendCode(): CommonStatusServiceState<Any>

    suspend fun validatePhoneWithSmsCode(smsCode: String): CommonStatusServiceState<Any>

    suspend fun updateCodeSecurity(newCodeSecurity: String): CommonStatusServiceState<Any>

}

class UpdateProfileRepositoryImpl(
    private val onboardingAPI: OnboardingAPI,
    private val pinAPI: UpdateCodeApi
) :
    UpdateProfileRepository {

    private val phoneNumber = Prefs.get(PROPERTY_PHONE_USER_LOGGED, "")
    override suspend fun sendCode(): CommonStatusServiceState<Any> {

        //Log.d("JHMM", "phoneNumber: ${phoneNumber}")

        val phoneNumberEncrypt = SendCodeRequestData(phoneNumber).encryptByGeneralKey()

        //Log.d("JHMM", "phoneNumberEncrypt: ${phoneNumberEncrypt}")

        val response = onboardingAPI.sendCode(phoneNumberEncrypt).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

        //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

        return try {
            getStatusServiceState<Any>(
                statusCode = responseDecrypted?.code,
                dataToWork = null,
                responseDecrypted?.message
            )
        }catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }

    }

    override suspend fun validatePhoneWithSmsCode(smsCode: String): CommonStatusServiceState<Any> {

        //Log.d("JHMM", "smsCode: ${smsCode}")

        val request = CodeValidateRequestData(
            phoneNumber, smsCode, 0, ""
        )

        //Log.d("JHMM", "request: ${request.toJson()}")

        val requestEncrypt = request.encryptByGeneralKey()

        //Log.d("JHMM", "requestEncrypt: ${requestEncrypt}")

        val response = onboardingAPI.validatePhone(requestEncrypt).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

        //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

        return try {
            getStatusServiceState<Any>(
                statusCode = responseDecrypted?.code,
                dataToWork = null,
                responseDecrypted?.message
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }

    }

    override suspend fun updateCodeSecurity(newCodeSecurity: String): CommonStatusServiceState<Any> {

        val account =
            Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

        Log.d("JHMM", "codeSecurity: ${newCodeSecurity}")

        val request = UpdateCodeSecurityRequest(
            biometrico = false,
            codigoNuevo = newCodeSecurity.encryptByKeyPass(),
            usuarioId = account.id
        )

        Log.d("JHMM", "request: ${request.toJson()}")

        val requestEncrypted = request.encryptByGeneralKey()

        Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

        val response = pinAPI.updateCodeSecurity(requestEncrypted).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecryptedText = response.decryptByGeneralKeyOnlyText()

        //Log.d("JHMM", "responseDecryptedText: ${responseDecryptedText}")

        val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

        //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

        return try {
            getStatusServiceState<Any>(
                statusCode = responseDecrypted?.code,
                dataToWork = null,
                responseDecrypted?.message
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}