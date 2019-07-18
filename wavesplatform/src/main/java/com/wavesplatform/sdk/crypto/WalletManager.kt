/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.crypto

import com.google.common.base.Joiner

import java.security.SecureRandom
import java.util.ArrayList
import java.util.logging.Logger
import kotlin.experimental.and

internal class WalletManager {

    companion object {

        private var instance: WalletManager? = null

        /**
         * @return Generated random secret seed-phrase, seed recovery phrase or backup seed phrase
         * and contains 15 different words from dictionary.
         */
        fun createWalletSeed(): String {
            try {
                val nbWords = 15
                val len = nbWords / 3 * 4
                val random = SecureRandom()
                val seed = ByteArray(len)
                random.nextBytes(seed)

                if (Words.list.size != 2048) {
                    throw IllegalArgumentException("WalletManager: Words list did not contain 2048 words")
                }

                return Joiner.on(" ").join(toMnemonic(seed, Words.list))
            } catch (e: Exception) {
                Logger.getLogger(WalletManager::class.java.name).warning(e.localizedMessage)
                return ""
            }
        }

        fun get(): WalletManager {
            if (instance == null) {
                instance = WalletManager()
            }
            return instance!!
        }

        @Throws(IllegalAccessException::class)
        private fun toMnemonic(entropy: ByteArray, allWords: List<String>): List<String> {
            if (entropy.size % 4 > 0)
                throw IllegalAccessException("Entropy length not multiple of 32 bits.")

            if (entropy.size == 0)
                throw IllegalAccessException("Entropy is empty.")

            // We take initial entropy of ENT bits and compute its
            // checksum by taking first ENT / 32 bits of its SHA256 hash.

            val hash = WavesCrypto.sha256(entropy)
            val hashBits = bytesToBits(hash)

            val entropyBits = bytesToBits(entropy)
            val checksumLengthBits = entropyBits.size / 32

            // We append these bits to the end of the initial entropy.
            val concatBits = BooleanArray(entropyBits.size + checksumLengthBits)
            System.arraycopy(entropyBits, 0, concatBits, 0, entropyBits.size)
            System.arraycopy(hashBits, 0, concatBits, entropyBits.size, checksumLengthBits)

            // Next we take these concatenated bits and split them into
            // groups of 11 bits. Each group encodes number from 0-2047
            // which is a position in a wordlist.  We convert numbers into
            // words and use joined words as mnemonic sentence.

            val words = ArrayList<String>()
            val nWords = concatBits.size / 11
            for (i in 0 until nWords) {
                var index = 0
                for (j in 0..10) {
                    index = index shl 1
                    if (concatBits[i * 11 + j])
                        index = index or 0x1
                }
                words.add(allWords[index])
            }

            return words
        }

        private fun bytesToBits(data: ByteArray): BooleanArray {
            val bits = BooleanArray(data.size * 8)
            for (i in data.indices) {
                for (j in 0..7) {
                    bits[i * 8 + j] = (data[i] and (1 shl 7 - j).toByte()) != 0.toByte()
                }
            }
            return bits
        }
    }
}
