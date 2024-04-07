package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class CodiMovResponse(
    @SerializedName("folioFinal") val folio:String? = "",
    @SerializedName("nombreVendedor") val vendor:String? = "",
    @SerializedName("nombreComprador") val owner:String? = "",
    @SerializedName("color") val color:String? = "",
    @SerializedName("monto") var amount:Double? = 0.0,
    @SerializedName("fecha_hora_solicitud")val requestDate:String? = "",
    @SerializedName("fecha_hora_procesamiento")val processDate:String?="",
    @SerializedName("estatus") val status:String? ="",
    @SerializedName("tipo") val type:String? = "",
    @SerializedName("concepto")val concept:String? = "",
    @SerializedName("referencia")val reference:String? ="",
    @SerializedName("claveRastreo")val cr:String? = "",
    @SerializedName("IDC") val idc:String? = null,
    @SerializedName("TYP") val typ:Int? = null,
    @SerializedName("SER") val ser:Int? = null,
    @SerializedName("ACC") val acc:String? = null,
    @SerializedName("BAN") val ban:Int? = null,
    @SerializedName("DEV") val dev:String? = null,
    @SerializedName("sePuedeDevolver") val isDev:Boolean? = null,
    var isPending: Boolean = false,
    var incomeFrom: String? = "qrPayment"
)
