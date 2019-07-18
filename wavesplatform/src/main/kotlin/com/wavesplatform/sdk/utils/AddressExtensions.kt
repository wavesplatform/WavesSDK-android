package com.wavesplatform.sdk.utils

import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import java.util.*

const val WAVES_PREFIX = "waves://"

fun String?.isValidWavesAddress(): Boolean {
    if (this.isNullOrEmpty()) return false
    return try {
        val bytes = WavesCrypto.base58decode(this)
<<<<<<< HEAD:wavesplatform/src/main/java/com/wavesplatform/sdk/utils/AddressExtensions.kt
        if (bytes.size == WavesCrypto.ADDRESS_LENGTH &&
                bytes[0] == WavesCrypto.ADDRESS_VERSION &&
                bytes[1] == WavesSdk.getEnvironment().chainId) {
            val checkSum = Arrays.copyOfRange(bytes,
                    bytes.size - WavesCrypto.CHECK_SUM_LENGTH, bytes.size)
            val checkSumGenerated = WavesCrypto.calcCheckSum(
                    bytes.copyOf(bytes.size - WavesCrypto.CHECK_SUM_LENGTH))
=======
        if (bytes.size == ADDRESS_LENGTH &&
                bytes[0] == ADDRESS_VERSION &&
                bytes[1] == WavesSdk.getEnvironment().chainId) {
            val checkSum = Arrays.copyOfRange(bytes, bytes.size - CHECK_SUM_LENGTH, bytes.size)
            val checkSumGenerated = WavesCrypto.calcCheckSum(bytes.copyOf(bytes.size - CHECK_SUM_LENGTH))
>>>>>>> aaf5fdd77b681f5b8a3bbc1e4dd3421d44c64277:wavesplatform/src/main/kotlin/com/wavesplatform/sdk/utils/AddressExtensions.kt
            Arrays.equals(checkSum, checkSumGenerated)
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}

fun String.isAlias(): Boolean {
    return this.contains("alias")
}

fun String.makeAsAlias(): String {
    return "alias:${WavesSdk.getEnvironment().chainId.toChar()}:$this"
}

fun String.parseAlias(): String {
    return this.substringAfterLast(":")
}