package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class AccountCodiData(
    @SerializedName("ds") val ds:DsData? = null,
    @SerializedName("tc") val tc:Int? = null,
    @SerializedName("cb") val cb:String? =null,
    @SerializedName("ci") val ci:Int? = null,
    @SerializedName("rv") val rv:Int? = null,
    @SerializedName("cr") val cr:String? = null,
    @SerializedName("nombreCDA") val name:String? = null,
    @SerializedName("hmac") val hmac:String? = null
)
