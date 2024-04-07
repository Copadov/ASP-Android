package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class RegData(@SerializedName("dv") val dv:Int? = null,
    @SerializedName("edoPet") val code:Int? = null,
    @SerializedName("gId") val gId:String? = null,
    @SerializedName("ia") val ia:InformationDetail? = null,
    @SerializedName("idH") val idH:String? = null,
    @SerializedName("nc") val nc:String? = null,
     @SerializedName("ncR") val ncR:String? = null)
