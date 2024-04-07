package asp.android.asppagos.firebase.model

import asp.android.asppagos.data.models.PendingPaymentPushNotification
import asp.android.asppagos.data.models.PendingPaymentPushNotificationDBModel
import com.google.gson.annotations.SerializedName

data class PushNotificationData(
    @SerializedName("detalleResultado") val resultDetail: PendingPaymentPushNotification? = null,
    @SerializedName("edoPet") val edoPet: Int? = null,
)

fun PushNotificationData.toPendingPaymentPushNotificationDBModel(): PendingPaymentPushNotificationDBModel {
    return PendingPaymentPushNotificationDBModel(
        idByService = resultDetail?.id,
        hp = resultDetail?.hp,
        e = resultDetail?.e,
        cc = resultDetail?.cc,
        mt = resultDetail?.mt,
        hs = resultDetail?.hs,
        pc = resultDetail?.pc,
        cr = resultDetail?.cr,
        cNc = resultDetail?.c?.nc,
        cDv = resultDetail?.c?.dv,
        cTc = resultDetail?.c?.tc,
        cCb = resultDetail?.c?.cb,
        cCi = resultDetail?.c?.ci,
        cNb = resultDetail?.c?.nb,
        vNc = resultDetail?.v?.nc,
        vNb = resultDetail?.v?.nb,
        vTc = resultDetail?.v?.tc,
        vCb = resultDetail?.v?.cb,
        vCi = resultDetail?.v?.ci,
        vNb2 = resultDetail?.v?.nb2,
        vCb2 = resultDetail?.v?.cb2,
        vTc2 = resultDetail?.v?.tc2,
        edoPet = edoPet
    )
}