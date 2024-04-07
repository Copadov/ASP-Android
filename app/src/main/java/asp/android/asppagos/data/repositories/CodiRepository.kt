package asp.android.asppagos.data.repositories

import asp.android.asppagos.data.interfaces.CodiAPI
import asp.android.asppagos.data.usecases.UseCaseResult

interface CodiRepository {
    suspend fun registroInicial(encriptedData : String) : UseCaseResult<String>
}

class CodiRepositoryImp(private val codiAPI: CodiAPI) : CodiRepository{

    override suspend fun registroInicial(encriptedData: String): UseCaseResult<String> {
        TODO("Not yet implemented")
    }


}