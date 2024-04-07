package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.LoginAPI
import asp.android.asppagos.data.models.CodeResponseData
import asp.android.asppagos.data.models.GenericException
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.decryptDataWithRecoverKey
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson

interface LoginRepository {
    suspend fun loginV2(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun verifiedPhone(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun recoverPass(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun validatePhone(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun sendCode(encriptedData: String): UseCaseResult<CodeResponseData>
}

class LoginRepositoryImpl(private val loginAPI: LoginAPI) : LoginRepository {
    override suspend fun loginV2(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = loginAPI.loginV2(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(exception = GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun verifiedPhone(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = loginAPI.verifiedPhone(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun recoverPass(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = loginAPI.recoverPass(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptDataWithRecoverKey(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun validatePhone(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = loginAPI.validatePhone(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun sendCode(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = loginAPI.sendCode(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }
}