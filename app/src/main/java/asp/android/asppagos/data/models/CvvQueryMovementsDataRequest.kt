package asp.android.asppagos.data.models

data class CvvQueryMovementsDataRequest(
    val medioAcceso: String,
    val fechaInicial: String,
    val maxMovimientos: String,
    val tipoTarjeta: String,
    val ClaveTipoCuenta: String,
    val header: HeaderX,
    val fechaFinal: String,
    val tipoMedioAcceso: String,
    val numeroTarjeta: String,
    val token: String
)