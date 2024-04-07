package asp.android.asppagos.data.models

import com.google.gson.annotations.SerializedName

data class AspTrackingEvent(
    @SerializedName("alias") val alias: String? = null,
    @SerializedName("dv") val dv: String? = null,
    @SerializedName("fechaHora") val dateHour: String? = null,
    @SerializedName("epoch") val epoch: Long? = null,
    @SerializedName("accion") val action: String? = null,
    @SerializedName("evento") val event: String? = null,
    @SerializedName("detalle") val detail: AspTrackingEventDetail? = null,
)

data class AspTrackingEventDetail(
    @SerializedName("detalle") val detail: String? = null,
    @SerializedName("etiqueta") val ticket: String? = null,
    @SerializedName("urlId") val urlId: String? = null,
    @SerializedName("conexion") val conection: String? = null,
    @SerializedName("infoAdicional") val aditionalInfo: String? = null,
    @SerializedName("estadoApp") val appState: String? = null,
    @SerializedName("folioCr") val folioCr: String? = null,
    // falta respuesta, contenido y su data

)

data class AspTrackingEventResponse(
    @SerializedName("code") val codigo: Int? = null,
    @SerializedName("mensaje") val message: String? = null,
)