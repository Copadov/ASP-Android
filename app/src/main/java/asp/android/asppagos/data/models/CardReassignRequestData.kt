package asp.android.asppagos.data.models

data class CardReassignRequestData(
    val ClaveEmpresa: String,
    val IDSolicitud: String,
    val calle: String,
    val calle2: String,
    val colonia: String,
    val cp: String,
    val estado: String,
    val header: HeaderXX,
    val municipio: String,
    val nti: String,
    val num_exterior: String,
    val num_interior: String,
    val numeroTarjeta: String,
    val numeroTarjetaExtra: String,
    val referencia: String,
    val tipo_entrega: String,
    val token: String
)