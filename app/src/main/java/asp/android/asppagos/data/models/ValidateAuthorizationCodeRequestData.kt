package asp.android.asppagos.data.models

data class ValidateAuthorizationCodeRequestData(
    val biometrico: Boolean,
    val codigo: String,
    val servicioId: Int,
    val usuarioId: String
)