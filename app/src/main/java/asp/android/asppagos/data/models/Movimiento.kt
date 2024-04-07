package asp.android.asppagos.data.models

data class Movimiento(
    val Autorizacion: String,
    val ClaveMovimiento: String,
    val ConceptoMovimiento: String,
    val DescripcionComercio: String,
    val DescripcionPais: String,
    val DescripcionPostOperacion: String,
    val EstatusPostOperacion: String,
    val FechaMovimiento: String,
    val IDMovimiento: String,
    val ImporteMovimiento: String,
    val ImporteOrigenMovimiento: String,
    val MCC: String,
    val MonedaMovimiento: String,
    val MonedaOrigenMovimiento: String,
    val NumeroAfiliacionComercio: String,
    val Observaciones: String,
    val PaisComercio: String,
    val Referencia: String,
    val SaldoDespuesMovimiento: String,
    val TipoMovimientoCargoAbono: String
)