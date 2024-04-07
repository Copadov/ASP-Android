package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class PaymentData(
    @SerializedName("id") val id:String? = null,
    @SerializedName("s") val s:Int? = null,
    @SerializedName("mc") val mc:String? = null,
    @SerializedName("idcn") val idcn:String? = null
)
