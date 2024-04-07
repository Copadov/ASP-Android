package asp.android.asppagos.data.models

data class CvvQueryDataRequest(
    val Idsolicitud: Int,
    val header: Header,
    val medioAcceso: String,
    val tarjeta: Long,
    val tipoMedioAcceso: String
)