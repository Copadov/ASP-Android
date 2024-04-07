package asp.android.asppagos.data.models.cellphone_refill


import com.google.gson.annotations.SerializedName

data class CellphoneRefillHeaderRequest(
    @SerializedName("idCanalAtencion") val idAttentionChannel: Int = 1,
    @SerializedName("idClaseCanalAtencion") val idAttentionChannelClass: Int = 0,
    @SerializedName("idComisionista") val idCommissionAgent: Int = 0,
    @SerializedName("idEmpresa") val idCompany: Int = 0,
    @SerializedName("idPuntoAtencion") val idAttentionPoint: Int = 0,
    @SerializedName("idResponsabilidad") val idResponsibility: Int = 0,
    @SerializedName("idSucursal") val idBranch: Int = 0,
    @SerializedName("idTransaccion") val idTransaction: Int = 0,
    @SerializedName("idUbicacion") val idLocation: Int = 0,
    @SerializedName("idUsuario") val idUser: Int = 0
)