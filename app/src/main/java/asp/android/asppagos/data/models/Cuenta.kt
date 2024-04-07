package asp.android.asppagos.data.models

data class Cuenta(
    val clabe: String,
    val cobro: String,
    val cuenta: String,
    val descripcion: String,
    val favoritosTransferencias: List<FavoritosTransferencia>,
    val historialTransferencias: List<Any>,
    val mostrarTarjetaAPP: Int,
    val pago: String,
    val producto: String,
    var tarjetas: MutableList<Tarjeta>,
    val telefono: String
)