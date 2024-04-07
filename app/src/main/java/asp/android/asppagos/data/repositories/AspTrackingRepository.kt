package asp.android.asppagos.data.repositories

import android.icu.text.SimpleDateFormat
import android.util.Log
import asp.android.asppagos.data.interfaces.AspTrackingAPI
import asp.android.asppagos.data.models.AspTrackingEvent
import asp.android.asppagos.data.models.AspTrackingEventDetail
import asp.android.asppagos.data.models.AspTrackingEventResponse
import asp.android.asppagos.utils.CODI_ALIAS_KEY
import asp.android.asppagos.utils.CODI_DV_KEY
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKey
import asp.android.asppagos.utils.EncryptUtils.decryptByGeneralKeyOnlyText
import asp.android.asppagos.utils.EncryptUtils.encryptByGeneralKey
import asp.android.asppagos.utils.PROPERTY_ACCOUNT_ENCRIPTED
import asp.android.asppagos.utils.PROPERTY_PHONE_USER_LOGGED
import asp.android.asppagos.utils.Prefs
import asp.android.asppagos.utils.toJson
import java.util.Calendar
import java.util.Date
import java.util.Locale

interface AspTrackingRepository {

    suspend fun consume(
        webService: String,
        typeResponse: AspTrackingRepositoryImpl.ConsumeServiceTypeResponse,
        response: String? = null
    )

    suspend fun inform(
        eventAction: AspTrackingRepositoryImpl.EventAction,
        ticket: String,
        aditionalInfo: String? = null
    )

    suspend fun showOrSelect()
    suspend fun close(ticket: String)
    suspend fun other()
}

class AspTrackingRepositoryImpl(private val aspTrackingAPI: AspTrackingAPI) :
    AspTrackingRepository {

    // Phone
    val phone = Prefs.get(PROPERTY_PHONE_USER_LOGGED, "")

    // Mask phone
    val maskPhone = try {
        val firstDigits = phone.substring(0, 2)
        val lastDigits = phone.substring((phone.length - 3), phone.length)
        val maskPhone = "${firstDigits}****${lastDigits}"
    } catch (exception: Exception) {
        ""
    }

    // Alias
    val alias = Prefs.get(CODI_ALIAS_KEY, maskPhone.toString())

    // Dev
    val dev = Prefs.get(CODI_DV_KEY, "-1")

    override suspend fun consume(
        webService: String,
        typeResponse: ConsumeServiceTypeResponse,
        response: String?
    ) {
        val request =
            AspTrackingEvent(
                alias = alias,
                dv = dev,
                dateHour = getStringDate(),
                epoch = getEpoch(),
                action = CONSUME,
                event = EventAction.WEB_SERVICE.action,
                detail = AspTrackingEventDetail(
                    ticket = webService,
                    conection = typeResponse.action,
                    detail = response
                )
            )

        //Log.d("JHMM", "request: ${request}")

        val requestEncrypted = request.encryptByGeneralKey()

        //Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

        val requestDecripted = requestEncrypted.decryptByGeneralKey<AspTrackingEvent>()

        //Log.d("JHMM", "requestDecripted: ${requestDecripted}")

        val response = aspTrackingAPI.trackingCodi(requestEncrypted).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<AspTrackingEventResponse>()
    }

    override suspend fun inform(eventAction: EventAction, ticket: String, aditionalInfo: String?) {
        val request =
            AspTrackingEvent(
                alias = alias,
                dv = dev,
                dateHour = getStringDate(),
                epoch = getEpoch(),
                action = INFORM,
                event = eventAction.action,
                detail = AspTrackingEventDetail(ticket = ticket, aditionalInfo = aditionalInfo)
            )

        //Log.d("JHMM", "request: ${request}")

        val requestEncrypted = request.encryptByGeneralKey()

        ///Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

        val requestDecripted = requestEncrypted.decryptByGeneralKey<AspTrackingEvent>()

        //Log.d("JHMM", "requestDecripted: ${requestDecripted}")

        val response = aspTrackingAPI.trackingCodi(requestEncrypted).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<AspTrackingEventResponse>()


    }

    override suspend fun showOrSelect() {
        TODO("Not yet implemented")
    }

    override suspend fun close(ticket: String) {
        val request =
            AspTrackingEvent(
                alias = alias,
                dv = dev,
                dateHour = getStringDate(),
                epoch = getEpoch(),
                action = INFORM,
                event = EventAction.APP_SESSION.action,
                detail = AspTrackingEventDetail(ticket = "CERRAR_SESION")
            )

        //Log.d("JHMM", "request: ${request}")

        val requestEncrypted = request.encryptByGeneralKey()

        //Log.d("JHMM", "requestEncrypted: ${requestEncrypted}")

        val requestDecripted = requestEncrypted.decryptByGeneralKey<AspTrackingEvent>()

        //Log.d("JHMM", "requestDecripted: ${requestDecripted}")

        val response = aspTrackingAPI.trackingCodi(requestEncrypted).await()

        //Log.d("JHMM", "response: ${response}")

        val responseDecrypted = response.decryptByGeneralKey<AspTrackingEventResponse>()
    }

    override suspend fun other() {
        TODO("Not yet implemented")
    }

    private fun getStringDate(): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val date = calendar.get(Calendar.DATE)
        val dateFormat = SimpleDateFormat("AA4AAMMDD HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getEpoch() = System.currentTimeMillis()

    enum class EventAction(val action: String) {
        WEB_SERVICE(action = "SERVICIO_WEB"),
        PUSH_NOTIFICATION(action = "NOTIFICACION_PUSH"),
        APP_SESSION(action = "SESION_APP")
    }

    enum class ConsumeServiceTypeResponse(val action: String) {
        // Consume service
        SUCCESS("RESPONSE_SUCCESS"),
        ERROR("RESPONSE_ERROR")
    }

    companion object {

        // Action
        const val INFORM = "INFORMAR"
        const val CONSUME = "CONSUME"
        const val SELECT_OR_SHOW = "SELECCIONAR_O_VISUALIZAR"
        const val CLOSE = "CERRAR"
        const val OTHER = "OTRA"

        // Ticket
        const val LOGIN_EVENT = "LOGIN"
        const val ENTER_CODI = "ENTER_CODI_MODULE"
        const val LEAVE_CODI = "LEAVE_CODI_MODULE"
        const val APP_BACKGROUND = "APP_IN_BACKGROUND"
        const val APP_FOREGROUND = "APP_IN_FOREGROUND"
        const val USER_CLOSE_APP = "USER_CLOSE_APP"
        const val INACTIVITY_CLOSE_APP = "INACTIVITY_CLOSE_APP"
        const val LOST_INTERNET_CONNECTION = "LOST_INTERNET_CONNECTION"
        const val GET_INTERNET_CONNECTION = "GET_INTERNET_CONNECTION"
        const val RECEIVE_PUSH_NOTIFICATION = "RECEIVE_PUSH_NOTIFICATION"
    }

}
