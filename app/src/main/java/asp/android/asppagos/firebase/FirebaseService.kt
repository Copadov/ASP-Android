package asp.android.asppagos.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.sha512
import asp.android.asppagos.R
import asp.android.asppagos.data.models.PendingPaymentPushNotificationDBModel
import asp.android.asppagos.data.models.codi.AccountCodiData
import asp.android.asppagos.data.models.codi.NotificationCodiData
import asp.android.asppagos.data.repositories.AspTrackingRepository
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl
import asp.android.asppagos.data.repositories.AspTrackingRepositoryImpl.Companion.RECEIVE_PUSH_NOTIFICATION
import asp.android.asppagos.firebase.model.PushNotificationData
import asp.android.asppagos.firebase.model.toPendingPaymentPushNotificationDBModel
import asp.android.asppagos.ui.activities.MainActivity
import asp.android.asppagos.utils.CODI_KEYSORUCE_KEY
import asp.android.asppagos.utils.IS_USER_LOGIN
import asp.android.asppagos.utils.Prefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.objectbox.Box
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.koin.android.ext.android.inject

class FirebaseService : FirebaseMessagingService() {

    private val aspTrackingRepository by inject<AspTrackingRepository>()
    private val pushNotificationDB by inject<Box<PendingPaymentPushNotificationDBModel>>()
    private val isUserLogged = Prefs.get(IS_USER_LOGIN, false)

    @Override
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        saveIntoMessageDb(remoteMessage)

        GlobalScope.async {
            try {
                aspTrackingRepository.inform(
                    eventAction = AspTrackingRepositoryImpl.EventAction.PUSH_NOTIFICATION,
                    ticket = RECEIVE_PUSH_NOTIFICATION
                )
            } catch(exception: Exception) {
                exception.printStackTrace()
            }
        }

        //Log.d("JHMM", "From: ${remoteMessage.from} body: ${remoteMessage.data} remoteMessage: ${remoteMessage}")
        try {
            val messageObject =
                Gson().fromJson(remoteMessage.data["data"], NotificationCodiData::class.java)
            when {
                messageObject.info != null && messageObject.infoAccount == null -> {
                    // BANXICO NOTIFICATION
                    showNotification(messageObject.body ?: "NONE")
                }

                messageObject.info == null && messageObject.infoAccount != null -> {
                    // Verification account
                    val bodyMessageEncrypt = messageObject.infoAccount.data
                    val cadenaKeysouce =
                        messageObject.infoAccount.cr + Prefs.get(CODI_KEYSORUCE_KEY, "")
                    val keysource = sha512(cadenaKeysouce)
                    bodyMessageEncrypt?.let {
                        val dataDecrypt = desencriptaInformacionB64(keysource, bodyMessageEncrypt)
                        dataDecrypt?.let {
                            val dataBenef = Gson().fromJson(it, AccountCodiData::class.java)
                            val messageCodiValidation = when (dataBenef.rv) {
                                1 -> "La cuenta se valido correctamente"
                                else -> "La cuenta se encuentra  pendiente por ser validada"
                            }
                            showNotification(messageCodiValidation)
                        }

                    }
                }

                else -> {}
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            } ?: run {
                e.printStackTrace()
            }
        }

    }

    @Override
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    private fun showNotification(messageBody: String) {

        //Log.d("JHMM", "message body: ${messageBody}")

        val requestCode = 0
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = getString(R.string.channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("CODI")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 1
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun saveIntoMessageDb(remoteMessage: RemoteMessage) {
        try {
            val data = Gson().fromJson(remoteMessage.data["data"], PushNotificationData::class.java)

            // Convert
            pushNotificationDB.put(data.toPendingPaymentPushNotificationDBModel())

        } catch(exception: Exception) {
            exception.printStackTrace()
        }
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}
