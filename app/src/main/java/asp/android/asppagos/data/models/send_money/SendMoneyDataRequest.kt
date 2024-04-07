package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class SendMoneyDataRequest(
    @SerializedName("correoBeneficiario") val email: String?,
    @SerializedName("idInstitucionBeneficiario") val idBank: Int?,
    @SerializedName("nombreBeneficiario") val name: String?,
    @SerializedName("nombreInstitucion") val bankName: String?,
    @SerializedName("numeroCuentaBeneficiario") val numberAccount: String?,
    @SerializedName("numeroCuentaCoDi") val codiNumberAccount: String?,
    @SerializedName("tipoCuentaBeneficiario") val typeAccount: Int?
)