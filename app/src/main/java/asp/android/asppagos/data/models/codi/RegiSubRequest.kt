package asp.android.asppagos.data.models.codi

data class RegiSubRequest(val nc:String, val dv:Int, val idH:String, val ia:InformationDetail
                          , val idN:String, val hmac:String, val idC:String, val epoch:Long
                          , val hash:String)
