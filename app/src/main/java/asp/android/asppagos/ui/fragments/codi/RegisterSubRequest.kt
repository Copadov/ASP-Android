package asp.android.asppagos.ui.fragments.codi

import asp.android.asppagos.data.models.codi.InformationDetail
import com.google.gson.annotations.SerializedName

data class RegisterSubRequest(
    @SerializedName("actualizaCodR") val codR:Int? = 1,
    @SerializedName("codR") val codeCodi:String? = null,
    @SerializedName("dv") val dv:Int? = null,
    @SerializedName("dvOmision")val dvOm:Int? = null,
    @SerializedName("dvR")val dvR:Int? = null,
    @SerializedName("edoPet")val edo:Int? = 0,
    @SerializedName("hmac") val hmac:String? = null,
    @SerializedName("ia") val ia: InformationDetail? = null,
    @SerializedName("idH")val idH:String? = null,
    @SerializedName("idN")val idN:String? = null,
    @SerializedName("nc")val nc:String? = null
)
