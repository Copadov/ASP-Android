package asp.android.asppagos.data.models

data class accountAssignDataRequest(
    val cuenta: String,
    val header: Header,
    val nombreEmbozar: String,
    val tipoTarjeta: String,
    val token: String
)