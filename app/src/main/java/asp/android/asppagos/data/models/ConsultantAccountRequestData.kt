package asp.android.asppagos.data.models

data class ConsultantAccountRequestData(
    val IDSolicitud: String,
    val header: HeaderXX,
    val medioAcceso: String,
    val numeroTarjeta: String,
    val tipoMedioAcceso: String,
    val tipoTarjeta: String
)