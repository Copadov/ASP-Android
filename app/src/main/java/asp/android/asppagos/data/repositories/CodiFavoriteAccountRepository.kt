package asp.android.asppagos.data.repositories

import android.content.Context
import android.util.Log
import asp.android.asppagos.R
import asp.android.asppagos.data.interfaces.BanxicoCodiFavoriteApi
import asp.android.asppagos.data.interfaces.FavoriteCodiAPI
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.codi.CodiValidationRequest
import asp.android.asppagos.data.models.configurations.CodiFavoriteAccountRequest
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.decryptByPasswordEncryptedByKeyPass
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByPasswordEncryptedByKeyPass
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson
import com.google.gson.Gson

interface CodiFavoriteAccountRepository {
    suspend fun codiFavoriteAccount(
        dvValue: String,
        hmacValue: String,
        ncValue: String
    ): CommonStatusServiceState<Any>
}

class CodiFavoriteAccountRepositoryImpl(
    private val codiFavoriteCodiAPI: FavoriteCodiAPI,
    private val banxicoCodiFavoriteApi: BanxicoCodiFavoriteApi,
    private val context: Context
) :
    CodiFavoriteAccountRepository {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    override suspend fun codiFavoriteAccount(
        dvValue: String,
        hmacValue: String,
        ncValue: String
    ): CommonStatusServiceState<Any> {
        return try {

            val dataCIf =
                CodiFavoriteAccountRequest(dv = dvValue, hmac = hmacValue, nc = ncValue);

            //Log.d("JHMM", "dataCIf: ${dataCIf.toJson()}")

            val responseBanxico =
                banxicoCodiFavoriteApi.registerCodiFavoriteAccountInBanxico(
                    Prefs[context.getString(R.string.codi_registra_app_omision)],
                    dataCIf
                ).await()

            when (responseBanxico.edoPet) {
                -1 -> return CommonStatusServiceState.Error(message = "No fue posible registrar dispositivo.")
                -3 -> return CommonStatusServiceState.Error(message = "Parametros de entrada incorrectos.")
                -4 -> return CommonStatusServiceState.Error(message = "Dispositivo no registrado previamente.")
            }

            //Log.d("JHMM", "responseBanxico: ${responseBanxico}")

            val dataCifEncrypted = dataCIf.encryptByPasswordEncryptedByKeyPass()

            //Log.d("JHMM", "dataCifEncrypted: ${dataCifEncrypted}")

            val request =
                CodiValidationRequest(account = account.cuenta.cuenta, data = dataCifEncrypted, channel = 1)

            //Log.d("JHMM", "request: ${request.toJson()}")

            val requestEncrypted = request.encryptByGeneralKey()

            //Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

            val response =
                codiFavoriteCodiAPI.registerCodiFavoriteAccountInASP(requestEncrypted).await()

            //Log.d("JHMM", "response: ${response.fromJson<CommonServiceResponse>()}")

            val responseDecrypted = response.fromJson<CommonServiceResponse>()

            //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

            getStatusServiceState<CommonStatusServiceState<Any>>(
                statusCode = responseDecrypted.code,
                message = responseDecrypted.message
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}