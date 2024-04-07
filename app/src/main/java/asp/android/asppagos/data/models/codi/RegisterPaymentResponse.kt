package asp.android.asppagos.data.models.codi

import com.google.gson.annotations.SerializedName

data class RegisterPaymentResponse(
    @SerializedName("id")val id:Int? = null,
    @SerializedName("folio")val folio:String? = null,
    @SerializedName("folioFinal")val transactionFolio:String? = null,
    @SerializedName("concepto")val concept:String? = null,
    @SerializedName("monto")val amount:Double? = null,
    @SerializedName("fechaCobro")val paymentDate:String? = null,
    @SerializedName("referencia")val reference:String? = null,
    @SerializedName("comision")val fee:Double? = null,
    @SerializedName("tipoCobroId")val paymentType:Int? = null,
    @SerializedName("numeroSerie")val serie:Int? = null,
    @SerializedName("cuentaCobroVendedor")val vendorAccount:String? = null,
    @SerializedName("nombreVendedor")val vendorName:String? = null,
    @SerializedName("claveInstitucionVendedor")val vendorCi:Int? = null,
    @SerializedName("tipoCuentaIdCobroVendedor")val tycV:Int? = null,
    @SerializedName("telefonoVendedor")val vendorCell:String? = null,
    @SerializedName("consecutivoVendedor")val vendorDv:Int? = null,
    @SerializedName("cuentaComprador")val owAccount:String? = null,
    @SerializedName("cuentaPagoComprador")val owCb:String? = null,
    @SerializedName("nombreComprador")val owName:String? = null,
    @SerializedName("claveInstitucionComprador")val owCi:Int? = null,
    @SerializedName("tipoCuentaIdPagoComprador")val owTyc:Int? = null,
    @SerializedName("telefonoComprador")val owCell:String? = null,
@SerializedName("consecutivoComprador")val owDv:String? = null)
