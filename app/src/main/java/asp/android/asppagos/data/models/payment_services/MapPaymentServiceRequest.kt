package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class MapPaymentServiceRequest(
    @SerializedName("codBarra")
    val codBarra: String?,
    @SerializedName("codigo")
    val codigo: String?,
    @SerializedName("cuenta")
    val cuenta: String?,
    @SerializedName("monto")
    val monto: String?,
    @SerializedName("montoCs")
    val montoCs: String?,
    @SerializedName("nemEmp")
    val nemEmp: String?,
    @SerializedName("numMed")
    val numMed: String?,
    @SerializedName("subEmp")
    val subEmp: String?
)