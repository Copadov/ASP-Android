package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.SaveFavoriteAPI
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.SendMoneyDataRequest
import asp.android.asppagos.data.models.send_money.SendMoneyRequest
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.toJson

interface FavoriteRepository {
    suspend fun addToFavoriteAccount(favoriteDataInfo: SendMoneyDataRequest): CommonStatusServiceState<Any>
}

class FavoriteRepositoryImpl(private val sendMoneyAPI: SaveFavoriteAPI) : FavoriteRepository {

    override suspend fun addToFavoriteAccount(favoriteDataInfo: SendMoneyDataRequest): CommonStatusServiceState<Any> {
        runCatching {

            //Log.d("JHMM", "favoriteData: ${favoriteDataInfo.toJson()}")

            val dataEncrypted = favoriteDataInfo.encryptByGeneralKey()
            //Log.d("JHMM", "favoriteData encrypted: ${dataEncrypted}")

            val request = SendMoneyRequest(codiNumberAccount = favoriteDataInfo.codiNumberAccount, data = dataEncrypted, idChannel = 1)
            //Log.d("JHMM", "reqeust: ${request}")

            val requestEncrypted = request.encryptByGeneralKey()
            //Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

            val response = sendMoneyAPI.addFavoriteAccountAsync(requestEncrypted).await()
            //Log.d("JHMM", "response: ${response}")

            val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()
            //Log.d("JHMM", "responseDecrypted: ${responseDecrypted}")

            return getStatusServiceState<Any>(statusCode = responseDecrypted?.code, dataToWork = null , message = responseDecrypted?.message )
        }.getOrElse {
            it.printStackTrace()
            return CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}