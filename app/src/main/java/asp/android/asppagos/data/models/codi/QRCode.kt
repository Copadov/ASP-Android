package asp.android.asppagos.data.models.codi

data class QRCode(val payment:CobroRequest? = null,val paymentCif:CobroReq? = null
                  ,val transaction: CobroCifrado? = null,val responsePayment:RegisterPaymentResponse? = null)
