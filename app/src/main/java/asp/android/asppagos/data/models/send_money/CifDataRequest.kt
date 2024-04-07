package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class CifDataRequest(
    @SerializedName("conceptoPago") val paymentConcept: String?,
    @SerializedName("correoBeneficiario") val beneficiaryEmail: String?,
    @SerializedName("cuentaBeneficiario") val beneficiaryAccount: String?,
    @SerializedName("cuentaOrdenante") val payerAccount: String?,
    @SerializedName("fechaCreacion") val createDate: Long?,
    @SerializedName("fechaNacimiento") val bornDate: DateDataRequest?,
    @SerializedName("idInstitucionBeneficiario") val beneficiaryInstitutionID: Int?,
    @SerializedName("idInstitucionOrdenante") val payerInstitutionID: Int?,
    @SerializedName("idTipoCuentaBeneficiario") val beneficiaryTypeAccountID: Int?,
    @SerializedName("idTipoCuentaOrdenante") val payerTypeAccountID: Int?,
    @SerializedName("idTipoPago") val paymentTypeID: Int?,
    @SerializedName("monto") val amount: Double?,
    @SerializedName("nombreBeneficiario") val beneficiaryName: String?,
    @SerializedName("nombreOrdenante") val payerName: String?,
    @SerializedName("rfcBeneficiario") val beneficiaryRFC: String?
)