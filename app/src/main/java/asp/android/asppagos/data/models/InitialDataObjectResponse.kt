package asp.android.asppagos.data.models

data class InitialDataObjectResponse(
    val version: String,
    val datos: MutableList<Dato>
)

data class Dato(
    val id: Int,
    val clave: String,
    val valor: String
)