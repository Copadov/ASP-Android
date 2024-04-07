package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.PaymentServicesAPI
import asp.android.asppagos.data.models.payment_services.GetServiceListRequest
import asp.android.asppagos.data.models.payment_services.HeaderServiceListRequest
import asp.android.asppagos.data.models.payment_services.PaymentServiceDataResponse
import asp.android.asppagos.data.models.payment_services.PaymentServiceRequest
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.data.models.send_money.CommonServiceResponse
import asp.android.asppagos.data.models.send_money.CommonStatusServiceState
import asp.android.asppagos.data.models.send_money.getStatusServiceState
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptStringByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.fromJson
import asp.android.asppagos.utils.toJson

interface PaymentServiceRepository {

    suspend fun getServiceList(): CommonStatusServiceState<Any>

    suspend fun paymentServices(request: PaymentServiceRequest): CommonStatusServiceState<Any>

}

class PaymentServicesRepositoryImpl(private val paymentServicesAPI: PaymentServicesAPI) :
    PaymentServiceRepository {

    override suspend fun getServiceList(): CommonStatusServiceState<Any> {
        return runCatching {
            val requestEncrypted = GetServiceListRequest(
                header = HeaderServiceListRequest(
                    idCanalAtencion = 1,
                    idClaseCanalAtencion = 0,
                    idComisionista = 0,
                    idEmpresa = 0,
                    idPuntoAtencion = 0,
                    idResponsabilidad = 0,
                    idSucursal = 0,
                    idTransaccion = 0,
                    idUbicacion = 0,
                    idUsuario = 0
                )
            ).toJson().encryptByGeneralKey()

            val response = paymentServicesAPI.getServicesList(requestEncrypted).await()

            val responseDecrypted = decryptStringByGeneralKey<CommonServiceResponse>(response)

            val dataResponseDecrypted = responseDecrypted?.data?.decryptByGeneralKey<List<ServiceDataResponse>>()

            getStatusServiceState<Any>(statusCode = responseDecrypted?.code, dataToWork = dataResponseDecrypted, responseDecrypted?.message)
        }.getOrElse {
            it.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

    override suspend fun paymentServices(request: PaymentServiceRequest): CommonStatusServiceState<Any> {
        return try {

            //Log.d("JHMM", "request: ${request}")

            val requestEncrypted = request.encryptByGeneralKey()
            //Log.d("JHMM", "request encrypted: ${requestEncrypted}")

            val response = paymentServicesAPI.paymentServices(requestEncrypted).await()
            //Log.d("JHMM", "response: ${response}")

            val responseDecrypted = response.decryptByGeneralKey<CommonServiceResponse>()
            //Log.d("JHMM", "response decrypted: ${responseDecrypted}")

            val dataDecrypted = responseDecrypted?.data?.decryptByGeneralKey<PaymentServiceDataResponse>()
            //Log.d("JHMM", "data decrypted: ${dataDecrypted}")

            getStatusServiceState<Any>(statusCode = responseDecrypted?.code, dataToWork = dataDecrypted, responseDecrypted?.message)
        } catch (exception: Exception) {
            exception.printStackTrace()
            CommonStatusServiceState.Error(message = "Ocurrió un error de comunicación, favor de intentar más tarde.")
        }
    }

}