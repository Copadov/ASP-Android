package asp.android.asppagos.data.interfaces

import asp.android.asppagos.data.models.OCRRequestData
import asp.android.asppagos.data.models.OCRResponseData
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OcrAPI {

    @Headers("Content-Type: application/json")
    @POST("obtener_datos_id")
    fun getOCRData(@Body ocrRequestData: OCRRequestData)
            : Deferred<OCRResponseData>
}