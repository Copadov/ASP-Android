package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class ProcessPaymentReq(@SerializedName("data")val data:String? = null,
@SerializedName("identificadorAviso") val identifier:Int?=0
                             ,@SerializedName("numeroCuentaAhorro")val account:String?=null)
