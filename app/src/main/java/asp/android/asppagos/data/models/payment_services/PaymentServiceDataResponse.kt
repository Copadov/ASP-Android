package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class PaymentServiceDataResponse(
    @SerializedName("msgHost")
    val msgHost: String?,
    @SerializedName("numAutorizacion")
    val numAutorizacion: String?,
    @SerializedName("referencia")
    val referencia: String?,
    @SerializedName("respuesta")
    val respuesta: String?,
    @SerializedName("resultado")
    val resultado: Int?,
    @SerializedName("resultadoCatel")
    val resultadoCatel: Int?
)