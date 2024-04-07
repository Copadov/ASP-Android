package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class RefundRequest(
    @SerializedName("cuentaAhorro") val account:String? = null,
    @SerializedName("folioFinal") val folio:String? = null,
    @SerializedName("montoDevolucion") val amount:Double? = null,
    @SerializedName("cuentaCobros") val ownerAccount:String? = null,
    @SerializedName("idTipoCuentaCobros") val typeAccount:Int? = 40
)
