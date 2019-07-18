package com.wavesplatform.sdk.crypto

import org.apache.commons.codec.binary.Base64
import org.spongycastle.crypto.BlockCipher
import org.spongycastle.crypto.BufferedBlockCipher
import org.spongycastle.crypto.InvalidCipherTextException
import org.spongycastle.crypto.PBEParametersGenerator
import org.spongycastle.crypto.engines.AESEngine
import org.spongycastle.crypto.engines.AESFastEngine
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.modes.CBCBlockCipher
import org.spongycastle.crypto.modes.OFBBlockCipher
import org.spongycastle.crypto.paddings.BlockCipherPadding
import org.spongycastle.crypto.paddings.ISO10126d2Padding
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.spongycastle.crypto.params.KeyParameter
import org.spongycastle.crypto.params.ParametersWithIV

import java.io.UnsupportedEncodingException
import java.security.SecureRandom

object AESUtil {

    const val MODE_CBC = 0
    const val MODE_OFB = 1
    private const val DEFAULT_PBKDF2_ITERATIONS_V2 = 5000
    private const val AESB_LOCK_SIZE = 4
    private const val KEY_BIT_LEN = 256

    /**
     * AES 256 PBKDF2 CBC iso10126 encryption
     */
    @Throws(Exception::class)
    fun encrypt(clearText: String?, password: String?, iterations: Int = DEFAULT_PBKDF2_ITERATIONS_V2): String {
        return encryptWithSetMode(clearText, password, iterations, MODE_CBC, ISO10126d2Padding())
    }

    /**
     * AES 256 PBKDF2 CBC iso10126 decryption
     * 16 byte IV must be prepended to cipherText - Compatible with crypto-js
     */
    @Throws(UnsupportedEncodingException::class, InvalidCipherTextException::class, AESUtil.DecryptionException::class)
    fun decrypt(cipherText: String?, password: String?, iterations: Int = DEFAULT_PBKDF2_ITERATIONS_V2): String {
        return decryptWithSetMode(cipherText, password, iterations, MODE_CBC, ISO10126d2Padding())
    }

    @Throws(InvalidCipherTextException::class, UnsupportedEncodingException::class, AESUtil.DecryptionException::class)
    fun decryptWithSetMode(cipherText: String?, password: String?, iterations: Int = DEFAULT_PBKDF2_ITERATIONS_V2, mode: Int,
                           padding: BlockCipherPadding?): String {

        if (password == null) {
            throw Exception("Password null")
        }

        if (cipherText == null) {
            throw Exception("Cipher text null")
        }

        val cipherData = Base64.decodeBase64(cipherText.toByteArray())

        //Separate the IV and cipher data
        val iv = copyOfRange(cipherData, 0, AESB_LOCK_SIZE * 4)
        val input = copyOfRange(cipherData, AESB_LOCK_SIZE * 4, cipherData.size)

        val generator = PKCS5S2ParametersGenerator()
        generator.init(
            PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(
                password.toCharArray()
            ), iv, iterations
        )
        val keyParam = generator.generateDerivedParameters(256) as KeyParameter

        val params = ParametersWithIV(keyParam, iv)

        val cipherMode: BlockCipher
        if (mode == MODE_CBC) {
            cipherMode = CBCBlockCipher(AESEngine())

        } else {
            cipherMode = OFBBlockCipher(AESEngine(), 128)
        }

        val cipher: BufferedBlockCipher
        if (padding != null) {
            cipher = PaddedBufferedBlockCipher(cipherMode, padding)
        } else {
            cipher = BufferedBlockCipher(cipherMode)
        }

        cipher.reset()
        cipher.init(false, params)

        // create a temporary buffer to decode into (includes padding)
        val buf = ByteArray(cipher.getOutputSize(input.size))
        var len = cipher.processBytes(input, 0, input.size, buf, 0)
        len += cipher.doFinal(buf, len)

        // remove padding
        val out = ByteArray(len)
        System.arraycopy(buf, 0, out, 0, len)

        // return string representation of decoded bytes
        val result = String(out, charset("UTF-8"))
        if (result.isEmpty()) {
            throw DecryptionException("Decrypted string is empty.")
        }

        return result
    }

    /**
     * @param key AES key (256 bit Buffer)
     * @param data e.g. "{'aaa':'bbb'}"
     * @return
     * @throws UnsupportedEncodingException
     */
    @Throws(UnsupportedEncodingException::class)
    fun encryptWithKey(key: ByteArray, data: String): ByteArray {

        val iv = salt
        val dataBytes = data.toByteArray(charset("UTF-8"))

        val keyParam = KeyParameter(key)
        val params = ParametersWithIV(keyParam, iv)

        val cipherMode = CBCBlockCipher(AESFastEngine())
        val cipher = PaddedBufferedBlockCipher(cipherMode, ISO10126d2Padding())
        cipher.reset()
        cipher.init(true, params)

        val outBuf = cipherData(cipher, dataBytes)

        // Concatenate iv
        val len1 = iv.size
        val len2 = outBuf.size
        val ivAppended = ByteArray(len1 + len2)
        System.arraycopy(iv, 0, ivAppended, 0, len1)
        System.arraycopy(outBuf, 0, ivAppended, len1, len2)

        return Base64.encodeBase64(ivAppended)
    }

    /**
     * @param key AES key (256 bit Buffer)
     * @param cipherText Base64 encoded concatenated payload + iv
     * @return
     * @throws InvalidCipherTextException
     * @throws UnsupportedEncodingException
     */
    @Throws(InvalidCipherTextException::class, UnsupportedEncodingException::class)
    fun decryptWithKey(key: ByteArray, cipherText: String): String {

        val dataBytesB64 = Base64.decodeBase64(cipherText.toByteArray(charset("UTF-8")))

        //Separate the IV and cipher data
        val iv = copyOfRange(dataBytesB64, 0, AESB_LOCK_SIZE * 4)
        val dataBytes = copyOfRange(dataBytesB64, AESB_LOCK_SIZE * 4, dataBytesB64.size)

        val keyParam = KeyParameter(key)
        val params = ParametersWithIV(keyParam, iv)

        val cipherMode = CBCBlockCipher(AESFastEngine())
        val cipher = PaddedBufferedBlockCipher(cipherMode, ISO10126d2Padding())
        cipher.reset()
        cipher.init(false, params)

        //Create a temporary buffer to decode into (includes padding)
        val buf = ByteArray(cipher.getOutputSize(dataBytes.size))
        var len = cipher.processBytes(dataBytes, 0, dataBytes.size, buf, 0)
        len += cipher.doFinal(buf, len)

        //Remove padding
        val out = ByteArray(len)
        System.arraycopy(buf, 0, out, 0, len)

        return String(out, charset("UTF-8"))
    }

    @Throws(UnsupportedEncodingException::class)
    fun stringToKey(string: String, iterations: Int): ByteArray {
        val salt = "salt".toByteArray(charset("UTF-8"))
        val generator = PKCS5S2ParametersGenerator()
        generator.init(
            PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(
                string.toCharArray()
            ), salt, iterations
        )
        val keyParam = generator.generateDerivedParameters(KEY_BIT_LEN) as KeyParameter
        return keyParam.key
    }

    /**
     * Use secure random to generate a 16 byte iv
     * @return
     */
    private val salt: ByteArray
        get() {
            val random = SecureRandom()
            val iv = ByteArray(AESB_LOCK_SIZE * 4)
            random.nextBytes(iv)
            return iv
        }

    private fun copyOfRange(source: ByteArray, from: Int, to: Int): ByteArray {
        val range = ByteArray(to - from)
        System.arraycopy(source, from, range, 0, range.size)
        return range
    }

    @Throws(Exception::class)
    private fun encryptWithSetMode(clearText: String?, password: String?, iterations: Int, mode: Int,
                                   padding: BlockCipherPadding?): String {

        if (password == null) {
            throw Exception("Password null")
        }

        if (clearText == null) {
            throw Exception("Clear text null")
        }

        // Use secure random to generate a 16 byte iv
        val random = SecureRandom()
        val iv = ByteArray(AESB_LOCK_SIZE * 4)
        random.nextBytes(iv)

        val clearBytes = clearText.toByteArray(charset("UTF-8"))

        val generator = PKCS5S2ParametersGenerator()
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password.toCharArray()), iv, iterations)

        val keyParam = generator.generateDerivedParameters(256) as KeyParameter

        val params = ParametersWithIV(keyParam, iv)

        val cipherMode: BlockCipher = if (mode == MODE_CBC) {
            CBCBlockCipher(AESEngine())
        } else {
            OFBBlockCipher(AESEngine(), 128)
        }

        val cipher: BufferedBlockCipher
        if (padding != null) {
            cipher = PaddedBufferedBlockCipher(cipherMode, padding)
        } else {
            cipher = BufferedBlockCipher(cipherMode)
        }

        cipher.reset()
        cipher.init(true, params)

        val outBuf = cipherData(cipher, clearBytes)

        // Append to IV to the output
        val len1 = iv.size
        val len2 = outBuf.size
        val ivAppended = ByteArray(len1 + len2)
        System.arraycopy(iv, 0, ivAppended, 0, len1)
        System.arraycopy(outBuf, 0, ivAppended, len1, len2)

        val raw = Base64.encodeBase64(ivAppended)
        return String(raw)
    }

    private fun cipherData(cipher: BufferedBlockCipher, data: ByteArray): ByteArray {
        val minSize = cipher.getOutputSize(data.size)
        val outBuf = ByteArray(minSize)
        val len1 = cipher.processBytes(data, 0, data.size, outBuf, 0)
        var len2 = -1
        try {
            len2 = cipher.doFinal(outBuf, len1)
        } catch (exception: InvalidCipherTextException) {
            exception.printStackTrace()
        }

        val actualLength = len1 + len2
        val result = ByteArray(actualLength)
        System.arraycopy(outBuf, 0, result, 0, result.size)
        return result
    }

    class DecryptionException(message: String) : Exception(message)
}