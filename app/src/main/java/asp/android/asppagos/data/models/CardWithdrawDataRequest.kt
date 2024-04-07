package asp.android.asppagos.data.models

data class CardWithdrawDataRequest(
    val header: HeaderX,
    val importe: String,
    val medioPago: String,
    val numero_tarjeta: String,
    val observaciones: String,
    val referenciaNumerica: String
)