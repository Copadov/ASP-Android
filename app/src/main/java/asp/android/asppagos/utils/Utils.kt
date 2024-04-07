package asp.android.asppagos.utils

import android.content.Context
import android.provider.Settings
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaKeySource
import asp.android.aspandroidcore.utils.generaKeySourceAccess
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun encriptData(dataToEncript: Any): String {

    val keyStart = Prefs[KEY, ""]
    val keySource = generaKeySource(keyStart)
    val jsonData = Gson().toJson(dataToEncript)
    val encryptData = keySource?.let { encriptaInformacionB64(it, jsonData) }

    return encryptData!!
}

fun encriptInitialData(dataToEncript: String): String {

    val keyStart = ""
    val keySource = generaKeySource(keyStart)
    val encryptData = keySource?.let { encriptaInformacionB64(it, dataToEncript) }

    return encryptData!!
}

fun encripRequesttInitialData(dataToEncript: Any, keyStart: String): String {

    val keySource = generaKeySource(keyStart)
    val jsonData = Gson().toJson(dataToEncript)
    val encryptData = keySource?.let { encriptaInformacionB64(it, jsonData) }

    return encryptData!!
}

fun decryptInitialData(dataToDecrypt: String, keyStart: String): String {

    val keySource = generaKeySource(keyStart)
    val decryptData = keySource?.let { desencriptaInformacionB64(it, dataToDecrypt) }!!

    return decryptData
}

fun encriptDataWithRecoverKey(dataToEncript: Any): String {

    val keyStart = Prefs[KEY_CAMBIA_PASS, ""]
    val keySource = generaKeySource(keyStart)
    val jsonData = Gson().toJson(dataToEncript)
    val encryptData = keySource?.let { encriptaInformacionB64(it, jsonData) }

    return encryptData!!
}

fun decryptDataWithRecoverKey(dataToDecrypt: String): String {

    val keyStart = Prefs[KEY_CAMBIA_PASS, ""]
    val keySource = generaKeySource(keyStart)
    val decryptData = keySource?.let { desencriptaInformacionB64(it, dataToDecrypt) }!!

    return decryptData
}

fun encriptPassword(dataToEncript: String): String {

    val keyStart = Prefs[KEY_PASS, ""]
    val keySource = generaKeySource(keyStart)
    val encryptData = keySource?.let { encriptaInformacionB64(it, dataToEncript) }

    return encryptData!!
}

fun encriptDataLocal(dataToEncript: Any, key1: String, key2: String): String {
    val keySource = generaKeySource(key1, key2)
    val jsonData = Gson().toJson(dataToEncript)
    val encryptData = keySource?.let { encriptaInformacionB64(it, jsonData) }

    return encryptData!!
}

fun decryptData(dataToDecrypt: String): String {

    val keyStart = Prefs[KEY, ""]
    val keySource = generaKeySource(keyStart)
    val decryptData = keySource?.let { desencriptaInformacionB64(it, dataToDecrypt) }!!

    return decryptData
}

fun decryptDataWithAccess(dataToDecrypt: String, k1: String, k2: String): String {

    val keySource = generaKeySourceAccess(k1, k2)
    val decryptData = keySource?.let { desencriptaInformacionB64(it, dataToDecrypt) }!!

    return decryptData
}

inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)


fun isAutomaticDateTimeEnabled(context: Context): Boolean {
    return try {
        // Get the value of the automatic date and time setting
        val autoDateTimeSetting: Int = Settings.Global.getInt(
            context.contentResolver, Settings.Global.AUTO_TIME
        )

        // Return true if automatic date and time is enabled
        autoDateTimeSetting == 1
    } catch (e: Settings.SettingNotFoundException) {
        // Handle exception if the setting is not found
        e.printStackTrace()
        false // Return false as a default value
    }
}