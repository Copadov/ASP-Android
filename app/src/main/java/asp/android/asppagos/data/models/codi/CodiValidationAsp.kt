package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class CodiValidationAsp(
    @SerializedName("cb") val cb:String? = null,
    @SerializedName("ci") val ci:Int? = null,
    @SerializedName("conceptoCobroOmision")val concept:String? = null,
    @SerializedName("cr") val cr:String? = null,
    @SerializedName("ds") val ds: DsData? = null,
    @SerializedName("edoPet") val edoPet:Int? = null,
    @SerializedName("estatusValidacionId")val statusId:Int? = null,
    @SerializedName("hmac") val hmac:String? = null,
    @SerializedName("tc") val tc:Int? = 40,
    @SerializedName("tipoCuentaId") val tyc:Int? = 2

)
