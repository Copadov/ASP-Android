package asp.android.asppagos.data.models

data class CodeValidateRequestData(
    val telefono: String,
    val token: String,
    val validaCuenta: Int,
    val curp: String,
    val idDispositivo: String = ""
)
