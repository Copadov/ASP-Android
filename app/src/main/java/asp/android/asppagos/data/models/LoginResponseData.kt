package asp.android.asppagos.data.models

data class LoginResponseData(
    val configuracionesASPpago: List<ConfiguracionesASPpago>,
    val correo: String,
    val costos: List<Costo>,
    val creditosActivos: List<CreditosActivo>,
    val cuenta: Cuenta,
    val id: String,
    val idCPU: String,
    val idCPV: String,
    val indicadorAdquiriente: Int,
    val listCatinsti: List<Catinsti>,
    val nombre: String,
    val nombreAdquiriente: String,
    val numeroNotificacionesPendientes: Int,
    val pinPago: Int,
    val properties: List<Property>,
    val referenciaAhorro: String,
    val urlAsp: List<UrlAsp>,
    val urlCoDi: List<UrlCoDi>
)