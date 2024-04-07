package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.OcrAPI
import asp.android.asppagos.data.models.GenericException
import asp.android.asppagos.data.models.OCRRequestData
import asp.android.asppagos.data.models.OCRResponseData
import asp.android.asppagos.data.usecases.UseCaseResult

interface OCRRepository {
    suspend fun getOCRData(ocrRequestData: OCRRequestData): UseCaseResult<OCRResponseData>
}

class OCRRepositoryImpl(private val ocrapi: OcrAPI) : OCRRepository {
    override suspend fun getOCRData(ocrRequestData: OCRRequestData): UseCaseResult<OCRResponseData> {
        return try {
            val result = ocrapi.getOCRData(ocrRequestData).await()
            UseCaseResult.Success(result)
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }
}