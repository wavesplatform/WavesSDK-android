/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.request.matcher

import android.util.Log

import com.google.common.primitives.Bytes
import com.google.common.primitives.Longs
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.crypto.WavesCrypto

/**
 * Cancel All Orders Request in DEX-matcher
 */
class CancelAllOrderRequest(
    /**
     * Current timestamp
     */
    @SerializedName("timestamp") var timestamp: Long = 0L,
    /**
     * Sender address or alias
     */
    @SerializedName("sender") var sender: String = "",
    /**
     * Order signature by account private key
     */
    @SerializedName("signature") var signature: String? = null
) {

    private fun toBytes(): ByteArray {
        return try {
            Bytes.concat(
                WavesCrypto.base58decode(sender),
                Longs.toByteArray(timestamp)
            )
        } catch (e: Exception) {
            Log.e("Wallet", "Couldn't create CancelAllOrderRequest bytes", e)
            ByteArray(0)
        }
    }

    fun sign(privateKey: ByteArray) {
        signature = WavesCrypto.base58encode(
            WavesCrypto.signBytesWithPrivateKey(toBytes(), WavesCrypto.base58encode(privateKey))
        )
    }
}
