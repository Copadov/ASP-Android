package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.FavoriteAccountApi
import asp.android.asppagos.data.models.FavoriteAccountRequest
import asp.android.asppagos.data.models.FavoritosTransferencia
import asp.android.asppagos.data.models.HeaderXXXX
import asp.android.asppagos.data.models.LoginResponseData
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.SingletonPassword
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson

interface FavoriteAccountRepository {
    suspend fun favoriteAccount(): CommonStatusServiceState<Any>
}

class FavoriteAccountRepositoryImpl(private val favoriteAccountApi: FavoriteAccountApi) :
    FavoriteAccountRepository {

    private val account =
        Gson().fromJson<LoginResponseData>(decryptData(Prefs[PROPERTY_ACCOUNT_ENCRIPTED]))

    private val userPass = SingletonPassword.getSessionPassword()

    override suspend fun favoriteAccount(): CommonStatusServiceState<Any> {
        return try {

            val request = FavoriteAccountRequest(
                header = HeaderXXXX(
                    idSesion = 0,
                    idResponsabilidad = 0,
                    idEmpresa = 0,
                    idSucursal = 0,
                    idClaseCanalAtencion = 1,
                    idCanalAtencion = 0,
                    idPuntoAtencion = 0,
                    idUbicacion = 0,
                    idComisionista = 0,
                    idTransaccion = 0,
                    ipHost = "172.17.5.9",
                    nameHost = "TEST-PC",
                    longitud = 100,
                    latitud = 200,
                    idBanco = "0",
                    idUsuario = 0,
                    usuarioClave = userPass
                ), cuenta = account.cuenta.cuenta
            )

            //Log.d("JHMM", "request: ${request.toJson()}")

            val requestEncrypted = request.encryptByGeneralKey()

            //Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

            val response = favoriteAccountApi.getFavoriteAccount(requestEncrypted).await()

            //Log.d("JHMM", "response: ${response}")

            val responseDecryptedOnlyText = response.decryptByGeneralKeyOnlyText()

            //Log.d("JHMM", "responseDecryptedOnlyText: ${responseDecryptedOnlyText}")

            val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()

            //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

            val dataDecrypted = responseDecrypted?.data?.decryptByGeneralKey<MutableList<FavoritosTransferencia>>()

            //Log.d("JHMM", "dataDecrypted: ${dataDecrypted}")

            getStatusServiceState<CommonStatusServiceState<Any>>(
                responseDecrypted?.code,
                dataDecrypted,
                responseDecrypted?.message
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}