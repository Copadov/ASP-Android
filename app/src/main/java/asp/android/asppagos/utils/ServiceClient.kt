package asp.android.asppagos.utils

import android.content.Context
import asp.android.asppagos.data.models.CodeResponseData
import asp.android.asppagos.data.modules.createCertificateHttpClient
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeoutException

interface MyService {
    @Headers("Content-Type: text/plain")
    @POST("obtenerDatosInicialesV")
    fun getInitialData(@Body encriptedData: String)
            : Deferred<String>
}

class ServiceClient(context: Context, aspuser: String, asppass: String, baseurl: String) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(createCertificateHttpClient(aspuser, asppass, context))
        .build()

    // Obtiene una instancia de la interfaz del servicio
    private val myService: MyService = retrofit.create(MyService::class.java)

    suspend fun getInitialData(encriptedData: String): CodeResponseData {
        try {
            return Gson().fromJson<CodeResponseData>(
                myService.getInitialData(encriptedData).await()
            )
        } catch (e: MyServiceException) {
            return CodeResponseData(mensaje = e.message!!)
        } catch (e: TimeoutException) {
            return CodeResponseData(mensaje = e.message!!)
        } catch (e: IOException) {
            return CodeResponseData(mensaje = e.message!!)
        } catch (e: Exception) {
            return CodeResponseData(mensaje = e.message!!)
        }
    }
}

class MyServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)
