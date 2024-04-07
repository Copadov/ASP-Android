package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class GetServiceListRequest(
    @SerializedName("header")
    val header: HeaderServiceListRequest?
)