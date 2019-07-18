package com.wavesplatform.sdk.utils

import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import java.util.*

const val ADDRESS_VERSION: Byte = 1
const val CHECK_SUM_LENGTH = 4
const val HASH_LENGTH = 20
const val ADDRESS_LENGTH = 1 + 1 + CHECK_SUM_LENGTH + HASH_LENGTH
const val WAVES_PREFIX = "waves://"

fun String?.isValidWavesAddress(): Boolean {
    if (this.isNullOrEmpty()) return false
    return try {
        val bytes = WavesCrypto.base58decode(this)
        if (bytes.size == ADDRESS_LENGTH &&
                bytes[0] == ADDRESS_VERSION &&
                bytes[1] == WavesSdk.getEnvironment().chainId) {
            val checkSum = Arrays.copyOfRange(bytes, bytes.size - CHECK_SUM_LENGTH, bytes.size)
            val checkSumGenerated = WavesCrypto.calcCheckSum(bytes.copyOf(bytes.size - CHECK_SUM_LENGTH))
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