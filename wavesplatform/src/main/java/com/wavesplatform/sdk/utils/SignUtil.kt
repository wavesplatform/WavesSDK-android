/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.utils

import com.google.common.primitives.Bytes
import com.google.common.primitives.Shorts
import com.wavesplatform.sdk.crypto.WavesCrypto
import java.nio.charset.Charset

object SignUtil {

    private val CHARSET = charset("UTF-8")

    fun textToBase58(attachmentPlainText: String?): String {
        return WavesCrypto.base58encode((attachmentPlainText ?: "").toByteArray(CHARSET))
    }

    fun textFromBase58(base58Text: String): String {
        return WavesCrypto.base58decode(base58Text).toString(CHARSET)
    }

    fun arrayWithSize(s: String?): ByteArray {
        return if (s != null && s.isNotEmpty()) {
            val b = WavesCrypto.base58decode(s)
            Bytes.concat(Shorts.toByteArray(b.size.toShort()), b)
        } else {
            Shorts.toByteArray(0.toShort())
        }
    }

    fun arrayOption(o: String): ByteArray {
        return if (org.apache.commons.lang3.StringUtils.isEmpty(o))
            byteArrayOf(0)
        else
            Bytes.concat(byteArrayOf(1), WavesCrypto.base58decode(o))
    }


    fun attachmentBytes(attachment: String): ByteArray {
        return if (attachment.isEmpty()) {
            byteArrayOf(0, 0)
        } else {
            attachment.toByteArray(CHARSET).arrayWithSize()
        }
    }

    fun recipientBytes(recipient: String, version: Byte, chainId: Byte): ByteArray {
        return if (recipient.length <= 30) {
            Bytes.concat(
                byteArrayOf(version),
                byteArrayOf(chainId),
                recipient.parseAlias()
                        .toByteArray(CHARSET)
                        .arrayWithSize()
            )
        } else {
            WavesCrypto.base58decode(recipient)
        }
    }
}
