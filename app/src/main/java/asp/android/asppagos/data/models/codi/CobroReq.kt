package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class CobroReq(@SerializedName("CRY")val cry:String? = null,val TYP:Int? = null
                    ,val ic:CobroContainerReq? = null,val v:Dev? = null)
