package asp.android.asppagos.data.models.cellphone_refill


import com.google.gson.annotations.SerializedName

data class CellphoneRefillServiceResponse(
    @SerializedName("codigo") val code: String?,
    @SerializedName("id") val id: Int?,
    @SerializedName("montos") val amountList: List<AmountRefillServiceResponse?>?,
    @SerializedName("nememp") val nememp: String?,
    @SerializedName("servicio") val service: String?,
    @SerializedName("subemp") val subemp: String?,
    @SerializedName("tipo_producto") val productType: String?,
    @SerializedName("url") val url: String?
)