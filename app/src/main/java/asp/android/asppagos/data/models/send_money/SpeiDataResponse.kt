package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class SpeiDataResponse(
    @SerializedName("beneficiario") val beneficiary: String?,
    @SerializedName("claveRastreo") val trackingCode: String?,
    @SerializedName("estatusOperacion") val operationStatus: String?,
    @SerializedName("monto") val amount: Double?,
    @SerializedName("referencia") val reference: String?,
    val isPaymentServicesOperation: Boolean = false,
    val isRechargeService: Boolean = false,
    val fifthValue: String?
)