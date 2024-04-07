package asp.android.asppagos.data.models

data class PinRequest(
    val biometrico: Boolean,
    val codigo: String,
    val servicioId: Int,
    val usuarioId: String
)

enum class PinRequestTypeTransaction(val id: Int) {
    PAYMENT_CREDIT(id = 1),
    SEND_MONEY(id = 2),
    CODI(id = 3),
    PAYMENT_SERVICES(id = 5),
    CELLPHONE_REFILL(id = 6),
    PIN_ASIGNATION(id = 7);
}