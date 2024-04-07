package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginAPI {

    @Headers("Content-Type: text/plain")
    @POST("logInV2")
    fun loginV2(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validacionDeTelefono")
    fun verifiedPhone(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("peticionCambioPassV2")
    fun recoverPass(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validaTelefono")
    fun validatePhone(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("enviarCodigo")
    fun sendCode(@Body encriptedData: String)
            : Deferred<String>
}