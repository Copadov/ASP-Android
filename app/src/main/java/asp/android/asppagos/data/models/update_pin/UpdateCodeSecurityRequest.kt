package asp.android.asppagos.data.models.update_pin

data class UpdateCodeSecurityRequest(
    val biometrico: Boolean,
    val codigoNuevo: String,
    val usuarioId: String
)