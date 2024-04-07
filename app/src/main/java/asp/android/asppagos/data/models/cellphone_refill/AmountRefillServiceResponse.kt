package asp.android.asppagos.data.models.cellphone_refill


import com.google.gson.annotations.SerializedName

data class AmountRefillServiceResponse(
    @SerializedName("id_monto") val idAmount: Int?,
    @SerializedName("monto") val amount: Double?,
    @SerializedName("producto") val product: String?
)