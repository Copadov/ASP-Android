package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentServicesAPI {

    @Headers("Content-Type: text/plain")
    @POST("listaServicios")
    fun getServicesList(@Body requestEncrypted: String): Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("procesa")
    fun paymentServices(@Body requestEncrypted: String): Deferred<String>

}