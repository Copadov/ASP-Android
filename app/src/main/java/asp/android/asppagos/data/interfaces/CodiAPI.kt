package asp.android.asppagos.data.interfaces

import asp.android.asppagos.data.models.codi.KeyDefData
import asp.android.asppagos.data.models.codi.RegistroResponse
import asp.android.asppagos.data.models.codi.GetStatusValidationResponse
import asp.android.asppagos.utils.PROPERTY_CODI_ENCRIPTED
import asp.android.asppagos.utils.Prefs
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface CodiAPI {

    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST
    fun registroInicial(@Url endpoint: String, @Body encriptedData:String) : Call<RegistroResponse>

    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST
    fun registerSub(@Url endpoint: String, @Body encriptedData: String) : Call<RegistroResponse>

    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST
    fun validationAccount(@Url endpoint: String, @Body encriptedData: String) : Call<RegistroResponse>


    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST
    fun getKeyCif(@Url endpoint: String, @Body encriptedData: String) : Call<KeyDefData>

    @Headers("Content-Type: text/plain;charset=UTF-8")
    @POST
    fun getStatusAccount(@Url endpoint: String, @Body encriptedData: String) : Call<GetStatusValidationResponse>

}