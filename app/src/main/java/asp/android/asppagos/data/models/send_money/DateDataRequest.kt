package asp.android.asppagos.data.models.send_money


import com.google.gson.annotations.SerializedName

data class DateDataRequest(
    @SerializedName("dia") val day: String?,
    @SerializedName("mes") val month: String?,
    @SerializedName("year") val year: String?
)