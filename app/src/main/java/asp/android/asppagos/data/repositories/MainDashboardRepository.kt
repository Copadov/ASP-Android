package asp.android.asppagos.data.repositories

import android.util.Log
import asp.android.asppagos.data.interfaces.MainAPI
import asp.android.asppagos.data.models.CodeResponseData
import asp.android.asppagos.data.models.GenericException
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson


interface MainDashboardRepository {
    suspend fun accountAssign(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardBlock(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardUnblock(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cvvQuery(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun movementsQuery(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardFund(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardWithdraw(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardAssign(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun queryMovements(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun queryCvv(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun assignPIN(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun requestReplacement(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun cardReassign(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun getBalance(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun consultAccount(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun getAddress(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun getBanksList(): UseCaseResult<CodeResponseData>
    suspend fun generateReportStatusMovementsV2(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun registerFinger(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun validateAuthorizationCode(encriptedData: String): UseCaseResult<CodeResponseData>
}

class MainDashboardRepositoryImpl(private val mainAPI: MainAPI) : MainDashboardRepository {

    override suspend fun accountAssign(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.accountAssign(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardBlock(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardBlock(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardUnblock(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardUnblock(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cvvQuery(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cvvQuery(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun movementsQuery(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.movementsQuery(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardFund(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardFund(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardWithdraw(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardWithdraw(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardAssign(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardAssign(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun queryMovements(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.movementsQuery(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun queryCvv(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cvvQuery(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun assignPIN(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.assignPIN(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun requestReplacement(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.requestReplacement(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun cardReassign(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.cardReassign(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun getBalance(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.getBalance(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun consultAccount(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.consultAccount(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun getAddress(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.getAddress(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun getBanksList(): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.getBanksList().await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun generateReportStatusMovementsV2(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.generateReportStatusMovementsV2(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun registerFinger(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            //Log.d("JHMM", "finger token encripted: ${encriptedData}")
            val result = mainAPI.registerFinger(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun validateAuthorizationCode(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = mainAPI.validateAuthorizationCode(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }
}