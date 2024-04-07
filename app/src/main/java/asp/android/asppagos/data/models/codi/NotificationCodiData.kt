package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class NotificationCodiData(
    @SerializedName("infoCuenta")val infoAccount:GetStatusValidationResponse? = null,
    @SerializedName("info")val info:InfoCifData? = null,
    @SerializedName("title")val title:String? = null,
    @SerializedName("body")val body:String? = null)
