package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class RequestCobro(@SerializedName("cobroStr") var payment:String?=null
                        ,@SerializedName("cuentaAhorro")val account:String?=null
                        ,@SerializedName("estatus")val status:Int?=1)
