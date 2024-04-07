package asp.android.asppagos.data.models

import com.google.gson.annotations.SerializedName

data class OCRRequestData(
    @SerializedName("id") private val Id : String,
    @SerializedName("idReverso") private val IdReverso : String
)