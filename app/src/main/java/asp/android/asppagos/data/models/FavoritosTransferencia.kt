package asp.android.asppagos.data.models

data class FavoritosTransferencia(
    val correoBeneficiario: String,
    val fechaHoraRegistro: String,
    val id: Int,
    val idInstitucionBeneficiario: Int,
    val nombreBeneficiario: String,
    val nombreInstitucion: String,
    val numeroCuentaBeneficiario: String,
    val numeroCuentaCoDi: String,
    val tipoCuentaBeneficiario: Int
)