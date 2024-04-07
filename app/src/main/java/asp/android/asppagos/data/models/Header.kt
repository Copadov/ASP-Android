package asp.android.asppagos.data.models

data class Header(
    val idCanalAtencion: String = "0",
    val idClaseCanalAtencion: String = "0",
    val idSesion: String = "0",
    val idComisionista: String = "0",
    val idEmpresa: String = "0",
    val idPuntoAtencion: String = "0",
    val idResponsabilidad: String = "0",
    val idSucursal: String = "0",
    val idTransaccion: String = "0",
    val idUbicacion: String = "0",
    val idUsuario: String = "0",
    val ipHost: String = "",
    val latitud: String = "0",
    val longitud: String = "",
    val nameHost: String = "",
    val usuarioClave: String = "",
    val idBanco: String = "",
    val numCuenta: String = "",

    )

data class Header2(
    val idTransaccion: Int = 0,
    val idCanalAtencion: Int = 2,
    val idResponsabilidad: Int = 0,
    val ipHost: String = "127.0.0.1",
    val idSucursal: Int = 0,
    val idUsuario: Int = 100,
    val idUbicacion: Int = 0,
    val idEmpresa: Int = 0,
    val nameHost: String = "localhost",
    val idComisionista: Int = 0,
    val idClaseCanalAtencion: Int = 0,
    val idPuntoAtencion: Int = 0,
)