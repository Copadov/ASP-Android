package asp.android.asppagos.data.models

data class LoginEncriptedData(
    val actualizaFavoritosSpei: Int= 0,
    val idCanal: Int?= 0,
    val password: String= "",
    val safety: Safety?,
    val token: String = "",
    val versionApp: String= "",
    val dispositivoId: String = "",
)