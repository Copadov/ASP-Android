package asp.android.asppagos.data.models

data class GetMovementsFileDataRequestData(
    val cuenta: String,
    val fechaFinal: Long,
    val fechaInicial: Long
)