package asp.android.asppagos.utils

import android.util.Log
import asp.android.aspandroidcore.utils.desencriptaInformacionB64
import asp.android.aspandroidcore.utils.encriptaInformacionB64
import asp.android.aspandroidcore.utils.generaHmacB64
import asp.android.aspandroidcore.utils.generaKeySource
import asp.android.aspandroidcore.utils.sha512
import com.google.gson.Gson

object EncryptUtils {

    // TODO: REMOVE THIS HARDCODE
    // USER INFO
    var deviceId = Prefs.get(DEVICE_ID_UNIQUE_GUID, "")
    var userPass = SingletonPassword.getSessionPassword() //Prefs.get(PROPERTY_PHONE_USER_LOGGED, "")

    // General Key

    private fun <T> encryptDataByGeneralKey(dataToEncrypt: T): String {
        val jsonData = Gson().toJson(dataToEncrypt)
        var encryptData = ""
        generaKeySource(Prefs[KEY])?.let {
            encryptData = encriptaInformacionB64(it, jsonData) ?: ""
        }
        return encryptData
    }

    fun <T> T.encryptByGeneralKey(): String {
        return encryptDataByGeneralKey(this)
    }

    inline fun <reified T> decryptStringByGeneralKey(dataToDecrypt: String): T? {
        runCatching {
            var decryptData = ""
            generaKeySource(Prefs[KEY])?.let { keySource ->
                desencriptaInformacionB64(keySource, dataToDecrypt)?.let {
                    decryptData = it
                }
            }
            return Gson().fromJson<T>(decryptData)
        }.getOrElse {
        it.printStackTrace()
            return null
        }
    }

    fun decryptStringByGeneralKeyOnlyText(dataToDecrypt: String): String {
        var decryptData = ""
        generaKeySource(Prefs[KEY])?.let { keySource ->
            desencriptaInformacionB64(keySource, dataToDecrypt)?.let {
                decryptData = it
            }
        }
        return decryptData
    }

    fun String.decryptByGeneralKeyOnlyText(): String {
        return decryptStringByGeneralKeyOnlyText(this)
    }

    inline fun <reified T> String.decryptByGeneralKey(): T? {
        return decryptStringByGeneralKey<T>(this)
    }

    // Key Pass

    private fun encryptStringFromKeyPass(dataToEncrypt: String): String {
        val keySource = generaKeySource(Prefs[KEY_PASS])
        var encryptData = ""
        keySource?.let {
            encryptData = encriptaInformacionB64(it, dataToEncrypt) ?: ""
        }
        return encryptData
    }

    private fun decryptStringFromKeyPass(dataToEncrypt: String): String {
        val keySource = generaKeySource(Prefs[KEY_PASS])
        var encryptData = ""
        keySource?.let {
            encryptData = desencriptaInformacionB64(it, dataToEncrypt) ?: ""
        }
        return encryptData
    }

    fun getEncryptUserPassByKeyPass(): String {
        return encryptStringFromKeyPass(userPass)
    }

    fun getDecryptUserPassByKeyPass(): String {
        return decryptStringFromKeyPass(userPass)
    }

    fun String.encryptByKeyPass(): String {
        return encryptStringFromKeyPass(this)
    }

    fun String.decryptByKeyPass(): String {
        return decryptStringFromKeyPass(this)
    }

    // Device ID not Encrypt and User Pass word encrypt by KeyPass

    private fun <T> encryptDataByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass(dataToEncrypt: T): String {
        var encryptData = ""
        val jsonData = Gson().toJson(dataToEncrypt)
        generaKeySource(deviceId, getEncryptUserPassByKeyPass())?.let {
            encryptData = encriptaInformacionB64(it, jsonData) ?: ""
        }
        return encryptData
    }

    fun <T> T.encryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass(): String {
        return encryptDataByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass(this)
    }

    private fun <T> encryptDataByPasswordEncryptedByKeyPass(dataToEncrypt: T): String {
        var encryptData = ""
        val jsonData = Gson().toJson(dataToEncrypt)
        generaKeySource(getEncryptUserPassByKeyPass())?.let {
            encryptData = encriptaInformacionB64(it, jsonData) ?: ""
        }
        return encryptData
    }

    fun <T> T.encryptByPasswordEncryptedByKeyPass(): String {
        return encryptDataByPasswordEncryptedByKeyPass(this)
    }

    inline fun <reified T> decryptStringByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass(dataToDecrypt: String): T {
        var encryptData = ""
        generaKeySource(deviceId, getEncryptUserPassByKeyPass())?.let {
            encryptData = desencriptaInformacionB64(it, dataToDecrypt) ?: ""
        }
        return Gson().fromJson<T>(encryptData)
    }

    inline fun <reified T> String.decryptByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass(): T {
        return decryptStringByDeviceIDNotEncryptAndUserPasswordEncryptByKeyPass<T>(this)
    }

    inline fun <reified T> decryptStringByPasswordEncryptedByKeyPass(dataToDecrypt: String): T {
        var encryptData = ""
        generaKeySource(getEncryptUserPassByKeyPass())?.let {
            encryptData = desencriptaInformacionB64(it, dataToDecrypt) ?: ""
        }
        return Gson().fromJson<T>(encryptData)
    }

    inline fun <reified T> String.decryptByPasswordEncryptedByKeyPass(): T {
        return decryptStringByPasswordEncryptedByKeyPass<T>(this)
    }


    //---------------------------->

    fun generaRegistraAppPorOmision(ncProperty: String, dvProperty: Int, cellphone: String): String? {
        val codR = Prefs.get(CODI_COD_R, "") // CodR
        val IdHardware = Prefs.get(DEVICE_ID_UNIQUE_GUID, "") // ID HARDWARE
        val numeroCelular = cellphone
        var keySource = ""
        val alias = ncProperty//9920561605L //NC
        val dv = dvProperty//1 //DV
        val consecutivo = String.format("%03d", dv)
        val cadenaHmac = alias + consecutivo
        keySource = sha512(codR)
        keySource = sha512(keySource + IdHardware + numeroCelular)
        val claveHmac = keySource.substring(64, 128)
        return generaHmacB64(cadenaHmac, claveHmac)
    }

    // Update initail data
    fun updateInitialData() {
        deviceId = Prefs.get(DEVICE_ID_UNIQUE_GUID, "")
        userPass = SingletonPassword.getSessionPassword()
    }

}