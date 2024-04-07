package asp.android.asppagos.ui.viewmodels.payment_services

import androidx.lifecycle.ViewModel
import asp.android.asppagos.data.models.payment_services.ServiceDataResponse
import asp.android.asppagos.ui.fragments.payment_services.PaymentServicesInfoBottomSheetModal

class PaymentServiceInfoViewModel : ViewModel() {

    private var serviceInfo: ServiceDataResponse? = null

    fun serviceInfo(serviceInfo: ServiceDataResponse?) {
        this.serviceInfo = serviceInfo
    }

    fun serviceInfo() = serviceInfo

    fun showScanAndInfoButtons(): Boolean {
        return serviceInfo?.id == CFE_ID || serviceInfo?.id == TELMEX_ID || serviceInfo?.id == TOTAL_PLAY_ID || serviceInfo?.id == SKY_ID // IDS for SHOW MODAL
    }

    fun getServiceReferenceType(): PaymentServicesInfoBottomSheetModal.ReferenceType {
        return when(serviceInfo?.id) {
            PaymentServicesInfoBottomSheetModal.ReferenceType.CFE.id -> PaymentServicesInfoBottomSheetModal.ReferenceType.CFE
            PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY.id -> PaymentServicesInfoBottomSheetModal.ReferenceType.TOTAL_PLAY
            PaymentServicesInfoBottomSheetModal.ReferenceType.SKY.id -> PaymentServicesInfoBottomSheetModal.ReferenceType.SKY
            PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX.id -> PaymentServicesInfoBottomSheetModal.ReferenceType.TELMEX
            else -> PaymentServicesInfoBottomSheetModal.ReferenceType.NONE
        }
    }

    companion object {
        const val CFE_ID = 15
        const val TELMEX_ID = 16
        const val TOTAL_PLAY_ID = 19
        const val SKY_ID = 12
    }

}