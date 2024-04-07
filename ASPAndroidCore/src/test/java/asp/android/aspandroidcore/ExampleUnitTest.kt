package asp.android.aspandroidcore

import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaKeySource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val gson = Gson()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun encriptData(dataToEncript: Any): String {

        val keyStart = "0okDE3a+#"
        val keySource = generaKeySource(keyStart)
        val jsonData = Gson().toJson(dataToEncript)
        val encryptData = keySource?.let { encriptaInformacionB64(it, jsonData) }

        return encryptData!!
    }

    @Test
    fun create_encryptdata() {
        val key = "b508a7bb87512279"
        val keyInicial = "0okDE3a+#"
        val keySource = generaKeySource(keyInicial)
        val requestModel = testModel(key)
        val jsonData = gson.toJson(requestModel)
        val datosEnc = keySource?.let { encriptaInformacionB64(it, jsonData) }
        val datosDesenc = keySource?.let {
            desencriptaInformacionB64(
                it,
                "ZgtWKA9+q1tfogDmHwFHnIhHJrVCPxey5dVbFYkiC401JYMRWd7bdNI0QIL8TE7uubWXDBQrtLihPnXGfSmAJZqzEcCPBXz8fd6cVOcwB10="
            )
        }
        assertEquals(datosDesenc, jsonData)
    }

    @Test
    fun create_decryptData() {
        val keyTest = "b508a7bb87512279"
        val keyInicial = "YIIp&369#"
        val json =
            "{\"codigo\":0,\"mensaje\":\"Ok\",\"data\":\"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\\u003d\\u003d\"}"
        val requestData = gson.fromJson(json, Respuesta::class.java)
        val request = requestData.data
        val dataDecrypted =
            generaKeySource(keyInicial)?.let { desencriptaInformacionB64(it, request) }
        val dataDecoded =
            generaKeySource(keyTest)?.let { desencriptaInformacionB64(it, dataDecrypted!!) }
        assertEquals(dataDecrypted, dataDecoded)
    }


    @Test
    fun create_enviarcodigo_encrypt_request() {
        val key = "2229106267"
        val keyInicial = "0okDE3a+#"
        val keySource = generaKeySource(keyInicial)
        val requestModel = mensajeRequest(key)
        val jsonData = gson.toJson(requestModel)
        val datosEnc = keySource?.let { encriptaInformacionB64(it, jsonData) }
        val datosDesenc = keySource?.let {
            desencriptaInformacionB64(
                it,
                "ew0KICAgICJhcGVsbGlkb01hdGVybm8iOiAiU09MT1JJTyIsDQogICAgImFwZWxsaWRvUGF0ZXJubyI6ICJHT01FWiIsDQogICAgImNlbHVsYXIiOiAiNjEyMTUyMzI5OCIsDQogICAgImNvZGlnb1Bvc3RhbCI6ICIiLA0KICAgICJjb2RpZ29Qcm9tb2Npb24iOiAiIiwNCiAgICAiY29sb25pYUlkIjogMCwNCiAgICAiY29sb25pYU5vbWJyZSI6ICIiLA0KICAgICJjdXJwIjogIkdPU0k4NDA4MDRIT0ZNTFMwNyIsDQogICAgImRvbWljaWxpbyI6ICJDIFBFUkEgMTg3IENPTkpIQUIgTEFTIEdBUlpBUyAyMzA3OSBMQSBQQVogQi5DUyIsDQogICAgImVtYWlsIjogImlzZ29tZXpAYXNwaW50ZWdyYW9wY2lvbmVzLmNvbSIsDQogICAgImFjdHVvQ3VlbnRhUHJvcGlhIjogIjEiLA0KICAgICJjb2RpZ29BdXRvcml6YWNpb24iOiAiMDEyMzIiLA0KICAgICJoZWFkZXIiOiB7DQogICAgICAgICJpZENhbmFsQXRlbmNpb24iOiAyLA0KICAgICAgICAiaWRDbGFzZUNhbmFsQXRlbmNpb24iOiAwLA0KICAgICAgICAiaWRDb21pc2lvbmlzdGEiOiAwLA0KICAgICAgICAiaWRFbXByZXNhIjogMCwNCiAgICAgICAgImlkUHVudG9BdGVuY2lvbiI6IDAsDQogICAgICAgICJpZFJlc3BvbnNhYmlsaWRhZCI6IDAsDQogICAgICAgICJpZFN1Y3Vyc2FsIjogMCwNCiAgICAgICAgImlkVHJhbnNhY2Npb24iOiAwLA0KICAgICAgICAiaWRVYmljYWNpb24iOiAwLA0KICAgICAgICAiaWRVc3VhcmlvIjogMA0KICAgIH0sDQogICAgImluZVZhbGlkYWRvIjogdHJ1ZSwNCiAgICAicHJpbWVyTm9tYnJlIjogIklTUkFFTCBBTlRPTklPIiwNCiAgICAicmZjIjogIkdPU0k4NDA4MDRIWjkiLA0KICAgICJnZW9sb2NhbGl6YWNpb24iOiB7DQogICAgICAgICJsYXRpdHVkIjogIjI0LjA4MjAzOSIsDQogICAgICAgICJsb25naXR1ZCI6ICItMTEwLjMwMDkwNiINCiAgICB9DQp9DQo="
            )
        }

        //val objectMap = datosDesenc?.let { Gson().fromJson<CodeResponseData>(it) }

        assertEquals(datosDesenc, datosEnc)
    }

    inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    data class testModel
        (
        val reqIni: String
    )

    data class mensajeRequest
        (
        val telefono: String
    )

    data class Respuesta
        (
        val codigo: Int,
        val mensaje: String,
        val data: String
    )
}

data class CodeResponseData(
    val codigo : Int,
    val mensaje: String
)
