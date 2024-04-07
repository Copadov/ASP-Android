package asp.android.asppagos.data.models

data class UnlockCardDataRequest(
    val header: Header,
    val motivoBloqueo: String,
    val numeroTarjeta: String
)