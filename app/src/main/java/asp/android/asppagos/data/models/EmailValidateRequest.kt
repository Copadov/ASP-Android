package asp.android.asppagos.data.models

data class EmailValidateRequest(
    val correoElectronico: String,
    val token: String,
    val rfc: String,
    val curp: String,
    val accion: String,
    val nombre: String
)
