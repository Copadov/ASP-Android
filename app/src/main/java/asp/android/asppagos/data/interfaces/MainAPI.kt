package asp.android.asppagos.data.interfaces

import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface MainAPI {

    @Headers("Content-Type: text/plain")
    @POST("asignarCuenta")
    fun accountAssign(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("bloquearTarjeta")
    fun cardBlock(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("desbloquearTarjeta")
    fun cardUnblock(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("consultaCvv")
    fun cvvQuery(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("consultarMovimientos")
    fun movementsQuery(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("fondearTarjeta")
    fun cardFund(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("retirarTarjeta")
    fun cardWithdraw(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("asignarOtraTarjeta")
    fun cardAssign(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("asignarNIP")
    fun assignPIN(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("solicitarReposicion")
    fun requestReplacement(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("reasignarTarjeta")
    fun cardReassign(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("obtenerSaldoAhorroSAV2")
    fun getBalance(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("consultaTarjeta")
    fun consultAccount(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("consultaDomicilio")
    fun getAddress(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @GET("obtenerSucursales")
    fun getBanksList()
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("generaReporteEstadoMovimientosV2")
    fun generateReportStatusMovementsV2(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("registrarTokenHuella")
    fun registerFinger(@Body encriptedData: String)
            : Deferred<String>

    @Headers("Content-Type: text/plain")
    @POST("validarCodigoAutorizacion")
    fun validateAuthorizationCode(@Body encriptedData: String)
            : Deferred<String>
}