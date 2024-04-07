package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class ServiceDataResponse(
    @SerializedName("codigo")
    val codigo: String?,
    @SerializedName("comision")
    val comision: Double?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("nememp")
    val nememp: String?,
    @SerializedName("servicio")
    val servicio: String?,
    @SerializedName("subemp")
    val subemp: String?,
    @SerializedName("tipo_producto")
    val tipoProducto: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("esFavorito")
    val esFavorito: Boolean?,
)