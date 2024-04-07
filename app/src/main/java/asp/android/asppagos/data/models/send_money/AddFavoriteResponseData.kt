package asp.android.asppagos.data.models.send_money

import com.google.gson.annotations.SerializedName

data class AddFavoriteResponseData(
    @SerializedName("codigo") val code: Int?,
    @SerializedName("data") val data: String?,
    @SerializedName("mensaje") val message: String?
)