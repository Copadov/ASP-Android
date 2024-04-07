package asp.android.asppagos.data.models.cellphone_refill

data class CompanyCellphoneRefills(
    val name: String,
    val urlImage: String,
    val planList: MutableList<CellphoneRefillServiceResponse>
)