package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class KeyDataRequest(
    @SerializedName("tipo") val type:Int? = null,
    @SerializedName("v") val vendor:InfoPayment? = null,
    @SerializedName("c") val buyer:InfoPayment? = null,
    @SerializedName("ic") val payment:PaymentData? = null,
    @SerializedName("hmac") val hmac:String? = null
)
