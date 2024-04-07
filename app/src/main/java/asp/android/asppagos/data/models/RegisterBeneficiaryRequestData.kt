package asp.android.asppagos.data.models

data class RegisterBeneficiaryRequestData(
    val beneficiarios: List<Beneficiario>,
    val cuenta: String,
    val header: HeaderXXX
)