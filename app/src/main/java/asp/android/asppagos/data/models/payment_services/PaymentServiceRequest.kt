package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class PaymentServiceRequest(
    @SerializedName("accesoId")
    val accesoId: String?,
    @SerializedName("header")
    val header: HeaderPaymentServiceRequest?,
    @SerializedName("map")
    val map: MapPaymentServiceRequest?,
    @SerializedName("Proceso")
    val proceso: String?,
    @SerializedName("Tipo")
    val tipo: String?
)