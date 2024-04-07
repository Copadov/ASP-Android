package asp.android.asppagos.data.models.cellphone_refill


import com.google.gson.annotations.SerializedName

data class CellphoneRefillRequest(
    @SerializedName("header") val header: CellphoneRefillHeaderRequest?
)