package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SpeiAPI {

    @Headers("Content-Type: text/plain")
    @POST("procesamientoSpeiSimpleV2")
    fun sendSpeiTransaction(@Body requestEncrypted: String): Deferred<String>

}