package asp.android.asppagos.data.models

data class ReplacementDataRequest(
    val IDSolicitud: String,
    val MedioAcceso: String,
    val TipoMedioAcceso: String,
    val header: HeaderX,
    val nombreEmbozado: String,
    val numeroTarjeta: String,
    val razonReposicion: String
)