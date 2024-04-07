package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class InfoPayment(val cb:String? = null, val ci:Int? = 90659,val dv:Int? = null
                       ,val nb:String? = null,val nc:String? = null,val tc:Int? = 40,
var COM:Int? = null,
@SerializedName("nb2") val nb2:String? = null,
@SerializedName("cb2") val cb2:String? = null,
@SerializedName("tc2") val tc2:String? = null)
