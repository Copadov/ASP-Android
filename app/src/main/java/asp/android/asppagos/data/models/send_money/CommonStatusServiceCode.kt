package asp.android.asppagos.data.models.send_money

import asp.android.asppagos.data.models.payment_services.ServiceDataResponse

sealed class CommonStatusServiceState<out T: Any> {
    data class Success<T: Any>(val value: Any?) : CommonStatusServiceState<T>()
    data class Error(val message: String): CommonStatusServiceState<Nothing>()
}

fun <T: Any> getStatusServiceState(statusCode: Int?, dataToWork: Any? = null, message: String?): CommonStatusServiceState<Any> {
    return when(statusCode) {
        0, 100 -> CommonStatusServiceState.Success(dataToWork)
        else -> CommonStatusServiceState.Error(message = message ?: "Ocurrió un error de comunicación, favor de intentar más tarde.")
    }
}