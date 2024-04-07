package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CellphoneRefillAPI {

    @Headers("Content-Type: text/plain")
    @POST("listaRecargas")
    fun getCellphonesRefills(@Body requestEncrypted: String): Deferred<String>

}