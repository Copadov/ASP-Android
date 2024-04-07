package asp.android.asppagos.data.models

data class CVVResponseData(
    val CodRespuesta: String,
    val DescRespuesta: String,
    val IDSolicitud: Int,
    val MedioAcceso: String,
    val Tarjeta: String,
    val TipoMedioAcceso: String,
    val ValorD2: String
)