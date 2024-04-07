package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class GetStatusValidationResponse(
    @SerializedName("infCif") val data: String? = null,
    @SerializedName("edoPet") val code: Int? = null,
    @SerializedName("infCifObj") val account: AccountCodiData? = null,
    @SerializedName("cr") val cr: String? = null
)