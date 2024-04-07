package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PinAPI {

    @Headers("Content-Type: text/plain")
    @POST("validarCodigoAutorizacion")
    fun checkUserPin(@Body requestEncrypted: String): Deferred<String>

}