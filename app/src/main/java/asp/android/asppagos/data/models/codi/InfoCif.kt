package asp.android.asppagos.data.models.codi


import com.google.gson.annotations.SerializedName

data class InfoCif(
    @SerializedName("id")
    val id: String?,
    @SerializedName("mc")
    val mc: String?
)