package asp.android.aspandroidcore.utils

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Calendar
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.Mac
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


fun sha512(base: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(base.toByteArray(charset("UTF-8")))
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        hexString.toString()
    } catch (ex: Exception) {
        throw RuntimeException(ex)
    }
}

fun sha512Hex(base: String): String? {
    return try {
        val digest = MessageDigest.getInstance("SHA-512")
        val hash = digest.digest(base.toByteArray(charset("UTF-8")))
        String(Hex.encodeHex(hash))
    } catch (ex: java.lang.Exception) {
        throw java.lang.RuntimeException(ex)
    }
}

fun decrypt(key: String, iv: String, encrypted: String): String? {
    //System.out.println("key :: " + key);
    //System.out.println("iv :: " + iv);
    //System.out.println("encrypted :: " + encrypted);
    var bytesOfKey: ByteArray? = null
    var ivBytes: ByteArray? = null
    var encryptedBytes: ByteArray? = null
    //byte[] bytesOfKey = DatatypeConverter.parseHexBinary(key);
    try {
        bytesOfKey = Hex.decodeHex(key.toCharArray())
    } catch (e: Exception) {
    }

    //byte[] ivBytes = DatatypeConverter.parseHexBinary(iv);
    try {
        ivBytes = Hex.decodeHex(iv.toCharArray())
    } catch (e: Exception) {
    }

    //byte[] encryptedBytes = DatatypeConverter.parseHexBinary(encrypted);
    try {
        encryptedBytes = Hex.decodeHex(encrypted.toCharArray())
    } catch (e: Exception) {
    }
    var resultBytes: ByteArray? = null
    var cipher: Cipher? = null
    var decrypted = ""
    try {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(bytesOfKey, "AES"),
            IvParameterSpec(ivBytes)
        )
        resultBytes = cipher.doFinal(encryptedBytes)
        decrypted = String(resultBytes)
    } catch (e: NoSuchAlgorithmException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: NoSuchPaddingException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: InvalidAlgorithmParameterException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: IllegalBlockSizeException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: BadPaddingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    return decrypted
}

fun decryptB64(key: String, iv: String, encrypted: String): String? {
    //System.out.println("key :: " + key);
    //System.out.println("iv :: " + iv);
    //System.out.println("encrypted :: " + encrypted);
    var bytesOfKey: ByteArray? = null
    var ivBytes: ByteArray? = null
    var encryptedBytes: ByteArray? = null
    //byte[] bytesOfKey = DatatypeConverter.parseHexBinary(key);
    try {
        bytesOfKey = Hex.decodeHex(key.toCharArray())
    } catch (e: Exception) {
    }

    //byte[] ivBytes = DatatypeConverter.parseHexBinary(iv);
    try {
        ivBytes = Hex.decodeHex(iv.toCharArray())
    } catch (e: Exception) {
    }

    //byte[] encryptedBytes = DatatypeConverter.parseHexBinary(encrypted);
    try {
        encryptedBytes = Base64.decodeBase64(encrypted.toByteArray())
    } catch (e: Exception) {
    }
    var resultBytes: ByteArray? = null
    var cipher: Cipher? = null
    var decrypted = ""
    try {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(bytesOfKey, "AES"),
            IvParameterSpec(ivBytes)
        )
        resultBytes = cipher.doFinal(encryptedBytes)
        decrypted = String(resultBytes)
    } catch (e: NoSuchAlgorithmException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: NoSuchPaddingException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: InvalidAlgorithmParameterException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: IllegalBlockSizeException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: BadPaddingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    return decrypted
}

fun cryptB64(key: String, iv: String, decrypted: String): String? {
    //System.out.println("key :: " + key);
    //System.out.println("iv :: " + iv);
    //System.out.println("encrypted :: " + decrypted);
    var bytesOfKey: ByteArray? = null
    var ivBytes: ByteArray? = null
    var decryptedBytes: ByteArray? = null
    try {
        bytesOfKey = Hex.decodeHex(key.toCharArray())
    } catch (e: Exception) {
    }
    try {
        ivBytes = Hex.decodeHex(iv.toCharArray())
    } catch (e: Exception) {
    }
    try {
        //decryptedBytes = org.apache.commons.codec.binary.Hex.decodeHex(decrypted.toCharArray());
        decryptedBytes = decrypted.toByteArray(charset("UTF-8"))
    } catch (e: Exception) {
        //Log.e("decryptedBytes", e.getMessage());
        try {
            decryptedBytes = decrypted.toByteArray(charset("UTF-8"))
        } catch (e1: UnsupportedEncodingException) {
            e1.printStackTrace()
        }
    }
    var resultBytes: ByteArray? = null
    var cipher: Cipher? = null
    var encrypted = ""
    try {
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(bytesOfKey, "AES"),
            IvParameterSpec(ivBytes)
        )
        resultBytes = cipher.doFinal(decryptedBytes)
        encrypted = String(Base64.encodeBase64(resultBytes))
    } catch (e: NoSuchAlgorithmException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: NoSuchPaddingException) {
        e.printStackTrace()
    } catch (e: InvalidKeyException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: InvalidAlgorithmParameterException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: IllegalBlockSizeException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: BadPaddingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }
    return encrypted
}

fun encryptB64(key: String, initVector: String, value: String): String? {
    try {
        //TODO ESTE METODO SE CREO PARA PROBAR
        val iv = IvParameterSpec(Hex.decodeHex(initVector.toCharArray()))
        val skeySpec = SecretKeySpec(Hex.decodeHex(key.toCharArray()), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(value.toByteArray())
        //System.out.println("encrypted string: " + Arrays.toString(Base64.encode(encrypted)));
        return String(Base64.encodeBase64(encrypted))
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
}

fun generaHmac(cadena: String, hmacKey: String): String? {
    //System.out.println("cadena :: " + cadena);
    //System.out.println("hmacKey :: " + hmacKey);
    val sha256_HMAC: Mac
    val hmac: String
    try {
        sha256_HMAC = Mac.getInstance("HmacSHA256")
        //SecretKeySpec secret_key = new SecretKeySpec( DatatypeConverter.parseHexBinary(hmacKey), "HmacSHA256");
        val secret_key = SecretKeySpec(Hex.decodeHex(hmacKey.toCharArray()), "HmacSHA256")
        sha256_HMAC.init(secret_key)
        val result = sha256_HMAC.doFinal(cadena.toByteArray(charset("UTF-8")))
        hmac = String(Hex.encodeHex(result))
    } catch (e: Exception) {
        return ""
    }
    return hmac
}

fun generaHmacB64(cadena: String, hmacKey: String): String? {
    //System.out.println("cadena :: " + cadena);
    //System.out.println("hmacKey :: " + hmacKey);
    val sha256_HMAC: Mac
    val hmac: String
    val hmacResult: String
    try {
        sha256_HMAC = Mac.getInstance("HmacSHA256")
        //SecretKeySpec secret_key = new SecretKeySpec( DatatypeConverter.parseHexBinary(hmacKey), "HmacSHA256");
        val secret_key = SecretKeySpec(Hex.decodeHex(hmacKey.toCharArray()), "HmacSHA256")
        sha256_HMAC.init(secret_key)
        val result = sha256_HMAC.doFinal(cadena.toByteArray(charset("UTF-8")))
        hmac = String(Hex.encodeHex(result))
        val hmacHex = Hex.decodeHex(hmac.toCharArray())
        val base64 = Base64.encodeBase64(hmacHex)
        hmacResult = String(base64)
    } catch (e: Exception) {
        return ""
    }
    return hmacResult
}

fun generaHmacB64FromKey(cadena: String, keySource: String): String? {
    //System.out.println("cadena :: " + cadena);
    //System.out.println("hmacKey :: " + hmacKey);
    val sha256_HMAC: Mac
    val hmac: String
    val hmacResult: String
    var hmacKey = ""
    try {
        hmacKey = keySource.substring(64, 128)
        sha256_HMAC = Mac.getInstance("HmacSHA256")
        //SecretKeySpec secret_key = new SecretKeySpec( DatatypeConverter.parseHexBinary(hmacKey), "HmacSHA256");
        val secret_key = SecretKeySpec(Hex.decodeHex(hmacKey.toCharArray()), "HmacSHA256")
        sha256_HMAC.init(secret_key)
        val result = sha256_HMAC.doFinal(cadena.toByteArray(charset("UTF-8")))
        hmac = String(Hex.encodeHex(result))
        val hmacHex = Hex.decodeHex(hmac.toCharArray())
        val base64 = Base64.encodeBase64(hmacHex)
        hmacResult = String(base64)
    } catch (e: Exception) {
        return ""
    }
    return hmacResult
}

fun base64Encoder(cadena: String): String? {
    var data = ByteArray(0)
    var result: String? = ""
    try {
        data = cadena.toByteArray(charset("UTF-8"))
        //String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        val base64 = Base64.encodeBase64(data)
        result = String(base64)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return result
}

fun base64Decoder(cadena: String): String? {
    var data = ByteArray(0)
    var result: String? = ""
    try {
        data = cadena.toByteArray(charset("UTF-8"))
        //String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        val base64 = Base64.decodeBase64(data)
        result = String(base64)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return result
}

fun base64DecoderHex(cadena: String): String? {
    var data = ByteArray(0)
    var result = ""
    try {
        data = cadena.toByteArray(charset("UTF-8"))
        //String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        val base64 = Base64.decodeBase64(data)
        val hexString = StringBuffer()
        for (i in base64.indices) {
            val hex = Integer.toHexString(0xff and base64[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        result = hexString.toString()
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
    return result
}

fun generaConsecutivo(s: String): String? {
    var consecutivo = s
    while (consecutivo.length < 3) {
        consecutivo = "0$consecutivo"
    }
    return consecutivo
}

fun xorHex(a: String, b: String): String? {

    val chars = CharArray(a.length)
    for (i in chars.indices) {
        chars[i] = toHex(fromHex(a[i]) xor fromHex(b[i]))
    }
    return String(chars)
}

private fun fromHex(c: Char): Int {
    if (c >= '0' && c <= '9') {
        return c - '0'
    }
    if (c >= 'A' && c <= 'F') {
        return c - 'A' + 10
    }
    if (c >= 'a' && c <= 'f') {
        return c - 'a' + 10
    }
    throw IllegalArgumentException()
}

private fun toHex(nybble: Int): Char {
    require(!(nybble < 0 || nybble > 15))
    return "0123456789ABCDEF"[nybble]
}

fun getTelefonoFromDEV(dev: String): String? {
    var telefono = ""
    var separador = 0
    separador = dev.indexOf("/")
    telefono = dev.substring(0, separador)
    return telefono
}

fun getConsecutivoFromDEV(dev: String): String? {
    var consecutivo = ""
    var separador = 0
    separador = dev.indexOf("/")
    consecutivo = dev.substring(separador + 1, dev.length)
    return consecutivo
}

private fun binarioToHex(bin: String): String? {
    var result = ""
    val resultado = StringBuilder()
    try {
        var i = 0
        while (i < bin.length - 1) {
            val numero = bin.substring(i, i + 4).toInt(2)
            val reprHex = Integer.toString(numero, 16)
            resultado.append(reprHex)
            i += 4
        }
        result = resultado.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

private fun decimalToBinario(numero: Int): String {
    val ala = StringBuilder()
    var n = numero
    var numerobinario = ""
    numerobinario = numerobinario + n % 2
    n = n / 2
    while (n >= 2) {
        numerobinario = numerobinario + n % 2
        n = n / 2
    }
    numerobinario = numerobinario + n
    var cadena = ala.append(numerobinario)
    cadena = ala.reverse()
    //System.out.println(cadena);
    return cadena.toString()
}

fun ComplementaBinario(b: String, r: Int): String {
    var binario = b
    while (binario.length < r) {
        binario = "0$binario"
    }
    return binario
}

fun encriptaInformacionB64(keySource: String, data: String): String? {
    var result: String? = ""
    return try {
        val key = keySource.substring(0, 32)
        val iv = keySource.substring(32, 64)
        result = cryptB64(key, iv, data)
        result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun desencriptaInformacionB64(keySource: String, data: String): String? {
    var result: String? = ""
    return try {
        val key = keySource.substring(0, 32)
        val iv = keySource.substring(32, 64)
        result = decryptB64(key, iv, data)
        result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}



fun getFolioMensajeCobro(): String? {
    var folio: String? = ""
    try {
        val fecha = Calendar.getInstance()
        val año = fecha[Calendar.YEAR]
        val soloAño = Integer.valueOf(año.toString().substring(2))
        val mes = fecha[Calendar.MONTH] + 1
        val dia = fecha[Calendar.DAY_OF_MONTH]
        val hora = fecha[Calendar.HOUR_OF_DAY]
        val minuto = fecha[Calendar.MINUTE]
        val segundo = fecha[Calendar.SECOND]
        val miliSegundo = fecha[Calendar.MILLISECOND]
        val miliSegundoMod = miliSegundo % 128
        val binAño = ComplementaBinario(decimalToBinario(soloAño), 7)
        val binMes = ComplementaBinario(decimalToBinario(mes), 4)
        val binDia = ComplementaBinario(decimalToBinario(dia), 5)
        val binHora = ComplementaBinario(decimalToBinario(hora), 5)
        val binMinuto = ComplementaBinario(decimalToBinario(minuto), 6)
        val binSegundo = ComplementaBinario(decimalToBinario(segundo), 6)
        val binMiliSeg = ComplementaBinario(decimalToBinario(miliSegundo), 7)
        val binMiliSegMod = ComplementaBinario(decimalToBinario(miliSegundoMod), 7)
        val cadena = binAño + binMes + binDia + binHora + binMinuto + binSegundo + binMiliSegMod
        folio = binarioToHex(cadena)
        //System.out.println("Fecha Actual: "+ dia + "/" + (mes) + "/" + año);
        //System.out.printf("Hora Actual: %02d:%02d:%02d %n", hora, minuto, segundo);
        //System.out.println("-------------Fecha desglosada----------------");
        //System.out.println("El año es: "+ soloAño + " Binario :: " + binAño);
        //System.out.println("El mes es: "+ mes + " Binario :: " + binMes);
        //System.out.println("El día es: "+ dia + " Binario :: " + binDia);
        //System.out.printf("La hora es: %02d %n", hora);
        //System.out.println("La hora es: " + hora + " Binario :: " + binHora);
        //System.out.println("El minuto es: " + minuto + " Binario :: " + binMinuto);
        //System.out.println("El segundo es: " + segundo + " Binario :: " + binSegundo);
        //System.out.println("El mili segundo es: " + miliSegundo + " Binario :: " + binMiliSeg);
        //System.out.println("El mili segundo mod 128: " + miliSegundoMod + " Binario :: " + binMiliSegMod);
        //System.out.println("cadena : " + cadena);
        //System.out.println("cadenaHex : " + binarioToHex(cadena));
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return folio
}

fun generaKeySource(s1: String, s2: String, s3: String): String? {
    var keySource = ""
    try {
        keySource = sha512(s1)
        keySource = sha512(keySource + s2 + s3)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keySource
}

fun generaKeySource(s1: String): String? {
    var keySource = ""
    try {
        keySource = sha512(s1)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keySource
}

fun generaKeySourceLocal(s1: String, s2: String?): String? {
    var keySourceLocal = ""
    try {
        keySourceLocal = sha512(s1 + s1)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keySourceLocal
}

fun generaKeySourceAccess(s1: String, s2: String): String? {
    var keySource = ""
    try {
        keySource = sha512(s1)
        keySource = sha512(keySource + s2)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keySource
}

fun generaKeySource(s1: String, s2: String?): String? {
    var keySource = ""
    try {

        keySource = sha512(s1)
        keySource = sha512(keySource + s2)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return keySource
}

fun generaMaskedKeySource(s1: String, s2: String): String? {
    var maskedKeySource: String? = ""
    try {
        val shaTmp = sha512Hex(s1)
        maskedKeySource = xorHex(shaTmp!!, s2)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return maskedKeySource
}

fun getKey(keySource: String): String? {
    var key = ""
    try {
        key = keySource.substring(0, 32)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return key
}

fun getIv(keySource: String): String? {
    var iv = ""
    try {
        iv = keySource.substring(32, 64)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return iv
}

fun getClaveHmac(keySource: String): String? {
    var claveHmac = ""
    try {
        claveHmac = keySource.substring(64, 128)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return claveHmac
}

fun getSecurePin(pin: String, referencia: String, key1: String?, key2: String?): String? {
    var pin = pin
    var securePin = ""
    try {
        val crypt = iso9564()
        crypt.setDebugEnable(0, 0)
        crypt.setPanPadChar('0')
        crypt.setPinPadChar('F')
        crypt.setKeyEncripterTralaterKey("1111222233334444", "1111111111111111")
        val referenciaTmp = "9001" + referencia + "01"
        pin = referenciaTmp.substring(4, 15) + "1" + pin
        securePin = crypt.getPvv(pin, key1, key2)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return securePin
}

fun redondeaCentabos(monto: Double, decimalesPermitidos: Int): Double? {
    var montoRendondeado = monto
    try {
        val splitter = monto.toString().split("\\.").toTypedArray()
        val numDecimales = splitter[1].length
        if (numDecimales > 2) {
            val factor = Math.pow(10.0, decimalesPermitidos.toDouble()).toLong()
            montoRendondeado = montoRendondeado * factor
            val tmp = Math.round(montoRendondeado)
            montoRendondeado = tmp.toDouble() / factor
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return montoRendondeado
}
