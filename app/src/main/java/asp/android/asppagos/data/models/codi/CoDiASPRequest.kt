package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class CoDiASPRequest(
    @SerializedName("numeroCuentaAhorro")val account:String? = null,
    @SerializedName("dataCif")val data:String?=null,
    @SerializedName("idCanal")val channel:Int? = 1,
    @SerializedName("cuentaAhorro") val  accountDevPayment:String? = null
)
