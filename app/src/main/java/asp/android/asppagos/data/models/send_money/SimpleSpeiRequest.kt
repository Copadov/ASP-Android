package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class SimpleSpeiRequest(
    @SerializedName("cuentaAhorro") val savingAccount: String?,
    @SerializedName("dataCif") val dataCif: String?,
    @SerializedName("idCanal") val idChannel: Int?,
    @SerializedName("header") val header: HeaderData?
)