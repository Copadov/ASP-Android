package asp.android.asppagos.data.models

data class ConsultAccountResponseData(
    val CLABE: String,
    val CodRespuesta: String,
    val Cuenta: String,
    val CuentaCacao: String,
    val DescRespuesta: String,
    val DescripcionStatus: String,
    val FechaVigencia: String,
    val IDSolicitud: String,
    val Nombre: String,
    val PrimerApellido: String,
    val SaldoActual: List<SaldoActual>,
    val Status: String,
    val Tarjeta: String,
    val TipoManufactura: String,
    val solicitante_id: String
)