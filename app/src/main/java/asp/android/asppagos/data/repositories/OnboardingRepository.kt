package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.OnboardingAPI
import asp.android.asppagos.data.models.CodeResponseData
import asp.android.asppagos.data.models.GenericException
import asp.android.asppagos.data.usecases.UseCaseResult
import asp.android.asppagos.utils.decryptData
import asp.android.asppagos.utils.fromJson
import com.google.gson.Gson

interface OnboardingRepository {
    suspend fun sendCode(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun validatePhone(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun validateEmail(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun verifiedPhone(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun registerSimpleAccount(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun validateCP(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun registerFinger(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun registerBeneficiary(encriptedData: String): UseCaseResult<CodeResponseData>
    suspend fun registerImageSimpleAccount(encriptedData: String): UseCaseResult<CodeResponseData>
}


class OnboardingRepositoryImpl(private val onboardingAPI: OnboardingAPI) : OnboardingRepository {

    override suspend fun sendCode(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.sendCode(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun validatePhone(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.validatePhone(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun validateEmail(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.validateEmail(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun verifiedPhone(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.verifiedPhone(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun registerSimpleAccount(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.registerSimpleAccount(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun validateCP(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.validateCP(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun registerFinger(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.registerFinger(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun registerBeneficiary(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.registerBeneficiary(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }

    override suspend fun registerImageSimpleAccount(encriptedData: String): UseCaseResult<CodeResponseData> {
        return try {
            val result = onboardingAPI.registerImageSimpleAccount(encriptedData).await()
            UseCaseResult.Success(Gson().fromJson<CodeResponseData>(decryptData(result)))
        } catch (ex: Exception) {
            ex.printStackTrace()
            UseCaseResult.Error(GenericException(message = "Ocurrió un error de comunicación, favor de intentar más tarde."))
        }
    }
}