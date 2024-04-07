package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SaveFavoriteAPI {

    @Headers("Content-Type: text/plain")
    @POST("registraFavoritoV2")
    fun addFavoriteAccountAsync(@Body requestEncrypted: String): Deferred<String>

}