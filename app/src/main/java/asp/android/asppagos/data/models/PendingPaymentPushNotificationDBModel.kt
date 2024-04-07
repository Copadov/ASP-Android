package asp.android.asppagos.data.models

import asp.android.asppagos.firebase.model.PushNotificationData
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PendingPaymentPushNotificationDBModel(
    @Id var id: Long = 0,
    val idByService: String? = null,
    val hp: Int? = null,
    val e: Int? = null,
    val cc: String? = null,
    val mt: Int? = null,
    val hs: Int? = null,
    val pc: String? = null,
    val cr: String? = null,
    val cNc: String? = null,
    val cDv: Int? = null,
    val cTc: Int? = null,
    val cCb: String? = null,
    val cCi: Int? = null,
    val cNb: String? = null,
    val vNc: String? = null,
    val vDv: Int? = null,
    val vTc: Int? = null,
    val vCb: String? = null,
    val vCi: Int? = null,
    val vNb: String? = null,
    val vNb2: String? = null,
    val vCb2: String? = null,
    val vTc2: String? = null,
    val edoPet: Int? = null,
)

fun PendingPaymentPushNotificationDBModel.toPushNotificationData(): PushNotificationData {
    return PushNotificationData(
        resultDetail = PendingPaymentPushNotification(
            id = idByService,
            hp = hp,
            e = e,
            cc = cc,
            mt = mt,
            hs = hs,
            pc = pc,
            cr = cr,
            c = CClass(nc = cNc, dv = cDv, tc = cTc, cb = cCb, ci = cCi, nb = cNb),
            v = VClass(
                nc = vNc,
                dv = vDv,
                tc = vTc,
                cb = vCb,
                ci = vCi,
                nb = vNb,
                nb2 = vNb2,
                cb2 = vCb2,
                tc2 = vTc2
            ),
        ),
        edoPet = edoPet
    )
}