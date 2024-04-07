package asp.android.asppagos.data.models

data class HeaderXX(
    val idBanco: String,
    val idCanalAtencion: String,
    val idClaseCanalAtencion: String,
    val idComisionista: String,
    val idEmpresa: String,
    val idPuntoAtencion: String,
    val idResponsabilidad: String,
    val idSesion: String,
    val idSucursal: String,
    val idTransaccion: String,
    val idUbicacion: String,
    val idUsuario: String,
    val ipHost: String,
    val longitud: String,
    val latitud: String = "0",
    val nameHost: String,
    val numCuenta: String,
    val usuarioClave: String
)