package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class SendMoneyRequest(
    @SerializedName("data") val data: String?,
    @SerializedName("idCanal") val idChannel: Int?,
    @SerializedName("numeroCuentaCoDi") val codiNumberAccount: String?
)