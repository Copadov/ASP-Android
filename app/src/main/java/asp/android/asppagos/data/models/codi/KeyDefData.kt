package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class KeyDefData(
    @SerializedName("claveEnmascCr") val emacCR:String? = null,
    @SerializedName("serieCertBmx") val certBmx:String? = null,
    @SerializedName("selloBmx") val printBmx:String? = null,
    @SerializedName("edoPet")val code:Int? = null)
