package asp.android.asppagos.data.interfaces

import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CodiAspAPI {

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("registroInicialV2")
    fun registroInicial(@Body encryptedData: String) : Call<String>

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("registroSubsecuenteV2")
    fun registroSubsequent(@Body encryptedData: String) : Call<String>

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("validacionDeCuentasV2")
    fun accountValidation(@Body encryptedData: String) : Call<String>

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("consultarEstatusCoDi")
    fun validationStatus(@Body encryptedData: String) : Call<String>

    //Metodos para enviar dinero (Pagar)

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("registraPagoGeneradoV2")
    fun registerPayment(@Body encryptedData: String) : Call<String>

    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("procesarPagoV2")
    fun processPayment(@Body encryptedData: String) : Call<String>

    /*METODOS COBRO CUANDO GENERA QR*/
    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("registraCobroGeneradoV2")
    fun registerCbroPayment(@Body encryptedData: String) : Call<String>
    @Headers("Content-Type: text/plain;charset=utf-8")
    @POST("obtenerReferenciaSerialCobroV2")
    fun getSerial(@Body encryptedData: String) : Call<String>


    //movimientos
    @Headers("Content-Type: text/plain")
    @POST("consultarOperacionesCoDi")
    fun getTransactionsCodi(@Body encryptedData: String):Call<String>

    @Headers("Content-Type: text/plain")
    @POST("consultarOperacionesCoDi")
    fun getTransactionsCodiDeferred(@Body encryptedData: String):Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validarCodigoAutorizacion")
    fun pinValidation(@Body encryptedData: String) : Call<String>

    @Headers("Content-Type: text/plain")
    @POST("procesarDevolucionV2")
    fun doRefund(@Body encryptedData: String) : Call<String>


}