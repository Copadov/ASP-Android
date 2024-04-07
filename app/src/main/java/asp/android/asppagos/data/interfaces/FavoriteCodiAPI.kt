package asp.android.asppagos.data.interfaces

import asp.android.asppagos.data.models.configurations.CodiFavoriteAccountRequest
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FavoriteCodiAPI {
    @Headers("Content-Type: text/plain")
    @POST("registraAppPorOmisionV2")
    fun registerCodiFavoriteAccountInASP(@Body requestEncrypted: String): Deferred<String>

}