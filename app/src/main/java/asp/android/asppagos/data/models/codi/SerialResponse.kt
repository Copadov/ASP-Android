package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class SerialResponse(@SerializedName("referenciaNumerica")val ref:String?= null
                          ,@SerializedName("serial")val ser:Int? = null)
