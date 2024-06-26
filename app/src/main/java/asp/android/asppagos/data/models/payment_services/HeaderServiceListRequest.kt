package asp.android.asppagos.data.models.payment_services


import com.google.gson.annotations.SerializedName

data class HeaderServiceListRequest(
    @SerializedName("idCanalAtencion")
    val idCanalAtencion: Int?,
    @SerializedName("idClaseCanalAtencion")
    val idClaseCanalAtencion: Int?,
    @SerializedName("idComisionista")
    val idComisionista: Int?,
    @SerializedName("idEmpresa")
    val idEmpresa: Int?,
    @SerializedName("idPuntoAtencion")
    val idPuntoAtencion: Int?,
    @SerializedName("idResponsabilidad")
    val idResponsabilidad: Int?,
    @SerializedName("idSucursal")
    val idSucursal: Int?,
    @SerializedName("idTransaccion")
    val idTransaccion: Int?,
    @SerializedName("idUbicacion")
    val idUbicacion: Int?,
    @SerializedName("idUsuario")
    val idUsuario: Int?
)