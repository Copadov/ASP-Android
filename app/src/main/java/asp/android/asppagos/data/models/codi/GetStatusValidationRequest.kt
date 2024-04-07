package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class GetStatusValidationRequest(
        @SerializedName("cr")val cr:String? = null,
        @SerializedName("ds") val ds:DsData? = null,
        @SerializedName("hmac")val hmac:String? = null
)
