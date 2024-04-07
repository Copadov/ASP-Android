package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.CodiAspAPI
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

interface CodiAspRepository{

    fun registroInicial(encryptedData: String, webService: (String) -> Unit) : Call<String>


    fun registroSubsequent(encryptedData: String, webService: (String) -> Unit) : Call<String>

    fun accountValidation(encryptedData: String, webService: (String) -> Unit) : Call<String>


    fun validationStatus(encryptedData: String, webService: (String) -> Unit) : Call<String>

    //Metodos para enviar dinero (Pagar)

    fun registerPayment(encryptedData: String, webService: (String) -> Unit) : Call<String>

    fun processPayment(encryptedData: String, webService: (String) -> Unit) : Call<String>

    /*METODOS COBRO CUANDO GENERA QR*/
    fun registerCbroPayment(encryptedData: String, webService: (String) -> Unit) : Call<String>

    fun getSerial(encryptedData: String, webService: (String) -> Unit) : Call<String>


    //movimientos
    fun getTransactionsCodi(encryptedData: String, webService: (String) -> Unit):Call<String>

    fun getTransactionsCodiDeferred(encryptedData: String, webService: (String) -> Unit):Deferred<String>

    fun pinValidation(encryptedData: String, webService: (String) -> Unit) : Call<String>

    fun doRefund(encryptedData: String, webService: (String) -> Unit) : Call<String>

}

class CodiAspRepositoryImp(private val codiAspAPI: CodiAspAPI): CodiAspRepository{
    override fun registroInicial(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("registroInicialV2")
        return codiAspAPI.registroInicial(encryptedData)
    }

    override fun registroSubsequent(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("registroSubsecuenteV2")
        return codiAspAPI.registroSubsequent(encryptedData)
    }

    override fun accountValidation(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("validacionDeCuentasV2")
        return codiAspAPI.accountValidation(encryptedData)
    }

    override fun validationStatus(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("consultarEstatusCoDi")
        return codiAspAPI.validationStatus(encryptedData)
    }

    override fun registerPayment(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("registraPagoGeneradoV2")
        return codiAspAPI.registerPayment(encryptedData)
    }

    override fun processPayment(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("procesarPagoV2")
        return codiAspAPI.processPayment(encryptedData)
    }

    override fun registerCbroPayment(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("registraCobroGeneradoV2")
        return codiAspAPI.registerCbroPayment(encryptedData)
    }

    override fun getSerial(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("obtenerReferenciaSerialCobroV2")
        return codiAspAPI.getSerial(encryptedData)
    }

    override fun getTransactionsCodi(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("consultarOperacionesCoDi")
        return codiAspAPI.getTransactionsCodi(encryptedData)
    }

    override fun getTransactionsCodiDeferred(encryptedData: String, webService: (String) -> Unit): Deferred<String> {
        webService.invoke("consultarOperacionesCoDi")
        return codiAspAPI.getTransactionsCodiDeferred(encryptedData)
    }

    override fun pinValidation(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("validarCodigoAutorizacion")
        return codiAspAPI.pinValidation(encryptedData)
    }

    override fun doRefund(encryptedData: String, webService: (String) -> Unit): Call<String> {
        webService.invoke("procesarDevolucionV2")
        return codiAspAPI.doRefund(encryptedData)
    }


}