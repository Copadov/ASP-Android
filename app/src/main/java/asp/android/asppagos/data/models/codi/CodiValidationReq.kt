package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class CodiValidationReq(
    val cb:String? = null,
    val ci:Int? = 90659,
   @SerializedName("conceptoCobroOmision") val concept:String? = "Cobro",
    val cr:String? = null,
    val ds:DsData? = null,
    val edoPet:Int? = 0,
    @SerializedName("estatusValidacionId")val status:Int? = 1,
    val hmac:String? = null ,
    val tc:Int? = 40,
    @SerializedName("tipoCuentaId") val tyc:Int? = 1
)
