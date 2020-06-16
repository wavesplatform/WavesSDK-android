package com.wavesplatform.sdk.utils

import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import java.util.Arrays

const val WAVES_PREFIX = "waves://"

fun String?.isValidWavesAddress(): Boolean {
    if (this.isNullOrEmpty()) return false
    return try {
        val bytes = WavesCrypto.base58decode(this)
        if (bytes.size == WavesCrypto.ADDRESS_LENGTH &&
            bytes[0] == WavesCrypto.ADDRESS_VERSION &&
            bytes[1] == WavesSdk.getEnvironment().chainId
        ) {
            val checkSum = Arrays.copyOfRange(
                bytes,
                bytes.size - WavesCrypto.CHECK_SUM_LENGTH, bytes.size
            )
            val checkSumGenerated = WavesCrypto.calcCheckSum(
                bytes.copyOf(bytes.size - WavesCrypto.CHECK_SUM_LENGTH)
            )
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
    val prefix = "alias:${WavesSdk.getEnvironment().chainId.toChar()}:"
    return if (this.startsWith(prefix)) {
        this
    } else {
        prefix + this
    }
}

fun String.parseAlias(): String {
    return this.substringAfterLast(":")
}
