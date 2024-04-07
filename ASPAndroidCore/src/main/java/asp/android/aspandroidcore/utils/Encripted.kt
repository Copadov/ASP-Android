package asp.android.aspandroidcore.utils

import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import kotlin.experimental.and

object Encrypted {
    @JvmStatic
    fun main(args: Array<String>) {
        println(getSecurePassword("123456", "0020028500"))
    }

    fun getSecurePassword(pswd: String, cta: String): String? {
        var generatedPassword: String? = null
        try {
            // Create SHA-256 instance
            val md = MessageDigest.getInstance("SHA-256")
            // Add "Salt"
            md.update(getSalt(pswd, cta))
            // Obtain password hash
            val bytes = md.digest(pswd.toByteArray())
            // Convert array from Bytes to hexadecimal
            val sb = StringBuilder()
            for (i in bytes.indices) {
                sb.append(Integer.toString((bytes[i] and 0xff.toByte()) + 0x100, 16).substring(1))
            }
            // Get complete formatted hash
            generatedPassword = sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        }
        return generatedPassword
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class)
    private fun getSalt(preHash: String, usuario: String): ByteArray {
        // Secure Random obtain a totally real random number
        val sr = SecureRandom.getInstance("SHA1PRNG")
        val secureHash = preHash + usuario
        try {
            sr.setSeed(secureHash.toByteArray(charset("us-ascii")))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        // Create byte[] for salt
        val salt = ByteArray(16)
        // Add last byte from the multiple posibilities.
        sr.nextBytes(secureHash.toByteArray())
        //return salt
        return salt
    }
}