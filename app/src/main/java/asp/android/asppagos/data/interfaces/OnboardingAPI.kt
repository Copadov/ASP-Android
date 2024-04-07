package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OnboardingAPI {

    @Headers("Content-Type: text/plain")
    @POST("enviarCodigo")
    fun sendCode(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validaTelefono")
    fun validatePhone(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validacionDeTelefono")
    fun verifiedPhone(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validacionDeCorreo")
    fun validateEmail(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validacionDeCURP")
    fun validateCURP(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("registroDeCuentaSimplificada")
    fun registerSimpleAccount(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("consultarCP")
    fun validateCP(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("registrarTokenHuella")
    fun registerFinger(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("altaBeneficiariosV2")
    fun registerBeneficiary(@Body encriptedData: String)
            : Deferred<String>


    @Headers("Content-Type: text/plain")
    @POST("registraImagenesCuentaSimplificada")
    fun registerImageSimpleAccount(@Body encriptedData: String)
            : Deferred<String>
}