package asp.android.asppagos.data.interfaces

import asp.android.asppagos.data.models.FavoriteCodiResponse
import asp.android.asppagos.data.models.configurations.CodiFavoriteAccountRequest
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface BanxicoCodiFavoriteApi {

    @Headers("Content-Type: text/plain")
    @POST
    fun registerCodiFavoriteAccountInBanxico(@Url endpoint: String, @Body request: CodiFavoriteAccountRequest): Deferred<FavoriteCodiResponse>
}