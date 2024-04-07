package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

class CobroRequest(val IDC: String? = null, var IDCN: String? = null, val DES: String? = null
                   , @SerializedName("AMO") var AMO: Double?, val DAT: Long? = null, val REF: Long? = null
                   , val COM: Int? = null, val TYP: Int? = null, val v:Vendor? = null,
                   @SerializedName("fechaLecturaQR")var qrReader:Long? = null){

}
