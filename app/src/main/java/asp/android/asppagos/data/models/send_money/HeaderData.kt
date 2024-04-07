package asp.android.asppagos.data.models.send_money

import com.google.gson.annotations.SerializedName

data class HeaderData(
    @SerializedName("idSesion") val sessionId: Long,
    @SerializedName("idEmpresa") val companyId: Long,
    @SerializedName("idResponsabilidad") val responsibilityId: Long,
    @SerializedName("usuarioClave") val userKey: String,
    @SerializedName("idUsuario") val userId: Long,
    @SerializedName("idClaseCanalAtencion") val channelClassId: Long,
    @SerializedName("idCanalAtencion") val channelId: Long,
    @SerializedName("idPuntoAtencion") val servicePointId: Long,
    @SerializedName("idUbicacion") val locationId: Long,
    @SerializedName("idSucursal") val branchId: Long,
    @SerializedName("idComisionista") val commissionAgentId: Long,
    @SerializedName("idTransaccion") val transactionId: Long,
    @SerializedName("ipHost") val hostIp: String,
    @SerializedName("nameHost") val hostName: String,
    @SerializedName("longitud") val longitude: Double,
    @SerializedName("latitud") val latitude: Double,
    @SerializedName("idBanco") val bankId: Long
)