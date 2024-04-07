package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FavoriteAccountApi {

    @Headers("Content-Type: text/plain")
    @POST("obtenerFavoritosCuenta")
    fun getFavoriteAccount(@Body requestEncrypted: String): Deferred<String>

}