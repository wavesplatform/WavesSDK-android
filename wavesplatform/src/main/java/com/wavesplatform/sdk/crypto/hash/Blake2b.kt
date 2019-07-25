/*
   A Java implementation of BLAKE2B cryptographic digest algorithm.

   Joubin Mohammad Houshyar <alphazero@sensesay.net>
   bushwick, nyc
   02-14-2014

   --

   To the extent possible under law, the author(s) have dedicated all copyright
   and related and neighboring rights to this software to the public domain
   worldwide. This software is distributed without any warranty.

   You should have received a copy of the CC0 Public Domain Dedication along with
   this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
*/

package com.wavesplatform.sdk.crypto.hash

import java.io.PrintStream
import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import java.util.*

interface Blake2b {
    // ---------------------------------------------------------------------
    // Specification
    // ---------------------------------------------------------------------
    interface Spec {
        companion object {
            /** pblock size of blake2b  */
            val param_bytes = 64

            /** pblock size of blake2b  */
            val block_bytes = 128

            /** maximum digest size  */
            val max_digest_bytes = 64

            /** maximum key sie  */
            val max_key_bytes = 64

            /** maximum salt size  */
            val max_salt_bytes = 16

            /** maximum personalization string size  */
            val max_personalization_bytes = 16

            /** length of h space vector array  */
            val state_space_len = 8

            /** max tree fanout value  */
            val max_tree_fantout = 0xFF

            /** max tree depth value  */
            val max_tree_depth = 0xFF

            /** max tree leaf length value.Note that this has uint32 semantics
             * and thus 0xFFFFFFFF is used as max value limit.  */
            val max_tree_leaf_length = -0x1

            /** max node offset value. Note that this has uint64 semantics
             * and thus 0xFFFFFFFFFFFFFFFFL is used as max value limit.  */
            val max_node_offset = -0x1L

            /** max tree inner length value  */
            val max_tree_inner_length = 0xFF

            /** initialization values map ref-Spec IV[i] -> slice iv[i*8:i*8+7]  */
            val IV = longArrayOf(
                0x6a09e667f3bcc908L,
                -0x4498517a7b3558c5L,
                0x3c6ef372fe94f82bL,
                -0x5ab00ac5a0e2c90fL,
                0x510e527fade682d1L,
                -0x64fa9773d4c193e1L,
                0x1f83d9abfb41bd6bL,
                0x5be0cd19137e2179L
            )

            /** sigma per spec used in compress func generation - for reference only  */
            val sigma = arrayOf(
                byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                byteArrayOf(14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3),
                byteArrayOf(11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4),
                byteArrayOf(7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8),
                byteArrayOf(9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13),
                byteArrayOf(2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9),
                byteArrayOf(12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11),
                byteArrayOf(13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10),
                byteArrayOf(6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5),
                byteArrayOf(10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0),
                byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                byteArrayOf(14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3)
            )
        }
    }

    // ---------------------------------------------------------------------
    // API
    // ---------------------------------------------------------------------
    // TODO add ByteBuffer variants

    /**  */
    fun update(input: ByteArray)

    /**  */
    fun update(input: Byte)

    /**  */
    fun update(input: ByteArray?, offset: Int, len: Int)

    /**  */
    fun digest(): ByteArray

    /**  */
    fun digest(input: ByteArray): ByteArray

    /**  */
    fun digest(output: ByteArray, offset: Int, len: Int)

    /**  */
    fun reset()

    // ---------------------------------------------------------------------
    // Blake2b Message Digest
    // ---------------------------------------------------------------------

    /** Generalized Blake2b digest.  */
    class Digest : Engine, Blake2b {
        private constructor(p: Param) : super(p)
        private constructor() : super()

        companion object {

            fun newInstance(): Digest {
                return Digest()
            }

            fun newInstance(digestLength: Int): Digest {
                return Digest(Param().setDigestLength(digestLength))
            }

            fun newInstance(p: Param): Digest {
                return Digest(p)
            }
        }
    }

    // ---------------------------------------------------------------------
    // Blake2b Message Authentication Code
    // ---------------------------------------------------------------------

    /** Message Authentication Code (MAC) digest.  */
    class Mac private constructor(p: Param) : Engine(p), Blake2b {

        companion object {

            /** Blake2b.MAC 512 - using default Blake2b.Spec settings with given key  */
            fun newInstance(key: ByteArray): Mac {
                return Mac(Param().setKey(key))
            }

            /** Blake2b.MAC - using default Blake2b.Spec settings with given key, with given digest length  */
            fun newInstance(key: ByteArray, digestLength: Int): Mac {
                return Mac(Param().setKey(key).setDigestLength(digestLength))
            }

            /** Blake2b.MAC - using default Blake2b.Spec settings with given java.security.Key, with given digest length  */
            fun newInstance(key: Key, digestLength: Int): Mac {
                return Mac(Param().setKey(key).setDigestLength(digestLength))
            }

            /** Blake2b.MAC - using the specified Parameters.
             * @param p asserted valid configured Param with key
             */
            fun newInstance(p: Param): Mac {
                assert(p != null) { "Param (p) is null" }
                assert(p.hasKey()) { "Param (p) not configured with a key" }
                return Mac(p)
            }
        }
    }

    // ---------------------------------------------------------------------
    // Blake2b Incremental Message Digest (Tree)
    // ---------------------------------------------------------------------

    /**
     * Note that Tree is just a convenience class; incremental hash (tree)
     * can be done directly with the Digest class.
     * <br></br>
     * Further node, that tree does NOT accumulate the leaf hashes --
     * you need to do that
     */
    class Tree
    /**
     *
     * @param fanout
     * @param depth
     * @param leaf_length size of data input for leaf nodes.
     * @param inner_length note this is used also as digest-length for non-root nodes.
     * @param digest_length final hash out digest-length for the tree
     */
        (
        internal val depth: Int,
        internal val fanout: Int,
        internal val leaf_length: Int,
        internal val inner_length: Int,
        internal val digest_length: Int
    ) {
        /** returns the Digest for root node  */
        val root: Digest
            get() {
                val depth = this.depth - 1
                val rootParam = treeParam().setNodeDepth(depth).setNodeOffset(0L).setDigestLength(digest_length)
                return Digest.newInstance(rootParam)
            }

        private fun treeParam(): Param {
            return Param().setDepth(depth).setFanout(fanout).setLeafLength(leaf_length).setInnerLength(inner_length)
        }

        /** returns the Digest for tree node @ (depth, offset)  */
        fun getNode(depth: Int, offset: Int): Digest {
            val nodeParam = treeParam().setNodeDepth(depth).setNodeOffset(offset.toLong()).setDigestLength(inner_length)
            return Digest.newInstance(nodeParam)
        }
    }

    // ---------------------------------------------------------------------
    // Engine
    // ---------------------------------------------------------------------
    open class Engine
    /** User provided Param for custom configurations  */
    @JvmOverloads internal constructor(
        /** configuration params  */
        private val param: Param = Param()
    ) : Blake2b {


        // ---------------------------------------------------------------------
        // Blake2b State(+) per reference implementation
        // ---------------------------------------------------------------------
        // REVU: address last_node TODO part of the Tree/incremental

        /** per spec  */
        private val h = LongArray(8)
        /** per spec  */
        private val t = LongArray(2)
        /** per spec  */
        private val f = LongArray(2)
        /** per spec (tree)  */
        private var last_node = false
        /** pulled up 2b optimal  */
        private val m = LongArray(16)
        /** pulled up 2b optimal  */
        private val v = LongArray(16)

        /** compressor cache buffer  */
        private val buffer: ByteArray
        /** compressor cache buffer offset/cached data length  */
        private var buflen: Int = 0
        /** digest length from init param - copied here on init  */
        private val outlen: Int
        /** to support update(byte)  */
        private val oneByte: ByteArray

        /** a little bit of semantics  */
        internal interface flag {
            companion object {
                val last_block = 0
                val last_node = 1
            }
        }

        init {
            assert(param != null) { "param is null" }
            this.buffer = ByteArray(Spec.block_bytes)
            this.oneByte = ByteArray(1)
            this.outlen = param.digestLength

            if (param.depth > Param.Default.depth) {
                val ndepth = param.nodeDepth
                val nxoff = param.nodeOffset
                if (ndepth == param.depth - 1) {
                    last_node = true
                    assert(param.nodeOffset == 0L) { "root must have offset of zero" }
                } else if (param.nodeOffset == (param.fanout - 1).toLong()) {
                    this.last_node = true
                }
            }

            initialize()

            //			Debug.dumpBuffer(System.out, "param bytes at init", param.getBytes());

        }

        private fun initialize() {
            // state vector h - copy values to address reset() requests
            System.arraycopy(param.initialized_H(), 0, this.h, 0, Spec.state_space_len)

            //			Debug.dumpArray("init H", this.h);
            // if we have a key update initial block
            // Note param has zero padded key_bytes to Spec.max_key_bytes
            if (param.hasKey) {
                this.update(param.key_bytes, 0, Spec.block_bytes)
            }
        }

        // ---------------------------------------------------------------------
        // interface: Blake2b API
        // ---------------------------------------------------------------------

        /** {@inheritDoc}  */
        override fun reset() {
            // reset cache
            this.buflen = 0
            for (i in buffer.indices) {
                buffer[i] = 0.toByte()
            }

            // reset flags
            this.f[0] = 0L
            this.f[1] = 0L

            // reset counters
            this.t[0] = 0L
            this.t[1] = 0L

            // reset state vector
            // NOTE: keep as last stmt as init calls update0 for MACs.
            initialize()
        }

        /** {@inheritDoc}  */
        override fun update(input: ByteArray?, offset: Int, len: Int) {
            var off = offset
            var length = len
            if (input == null) {
                throw IllegalArgumentException("input buffer (b) is null")
            }
            // zero or more calls to compress
            // REVU: possibly the double buffering of c-ref is more sensible ..
            //       regardless, the hotspot is in the compress, as expected.
            while (length > 0) {
                if (buflen == 0) {
                    // try compressing direct from input ?
                    while (length > Spec.block_bytes) {
                        this.t[0] += Spec.block_bytes.toLong()
                        this.t[1] += (if (this.t[0] == 0L) 1 else 0).toLong()
                        compress(input, off)
                        length -= Spec.block_bytes
                        off += Spec.block_bytes
                    }
                } else if (buflen == Spec.block_bytes) {
                    /* flush */
                    this.t[0] += Spec.block_bytes.toLong()
                    this.t[1] += (if (this.t[0] == 0L) 1 else 0).toLong()
                    compress(buffer, 0)
                    buflen = 0
                    continue
                }

                // "are we there yet?"
                if (length == 0) return

                val cap = Spec.block_bytes - buflen
                val fill = if (length > cap) cap else length
                System.arraycopy(input, off, buffer, buflen, fill)
                buflen += fill
                length -= fill
                off += fill
            }
        }

        /** {@inheritDoc}  */
        override fun update(b: Byte) {
            oneByte[0] = b
            update(oneByte, 0, 1)
        }

        /** {@inheritDoc}  */
        override fun update(input: ByteArray) {
            update(input, 0, input.size)
        }

        /** {@inheritDoc}  */
        override fun digest(output: ByteArray, off: Int, len: Int) {
            // zero pad last block; set last block flags; and compress
            System.arraycopy(zeropad, 0, buffer, buflen, Spec.block_bytes - buflen)
            if (buflen > 0) {
                this.t[0] += buflen.toLong()
                this.t[1] += (if (this.t[0] == 0L) 1 else 0).toLong()
            }

            this.f[flag.last_block] = -0x1L
            this.f[flag.last_node] = if (this.last_node) -0x1L else 0x0L

            // compres and write final out (truncated to len) to output
            compress(buffer, 0)
            hashout(output, off, len)

            reset()
        }

        /** {@inheritDoc}  */
        @Throws(IllegalArgumentException::class)
        override fun digest(): ByteArray {
            val out = ByteArray(outlen)
            digest(out, 0, outlen)
            return out
        }

        /** {@inheritDoc}  */
        override fun digest(input: ByteArray): ByteArray {
            update(input, 0, input.size)
            return digest()
        }

        // ---------------------------------------------------------------------
        // Internal Ops
        // ---------------------------------------------------------------------

        /**
         * write out the digest output from the 'h' registers.
         * truncate full output if necessary.
         */
        private fun hashout(out: ByteArray, offset: Int, hashlen: Int) {
            // write max number of whole longs
            val lcnt = hashlen.ushr(3)
            var v: Long = 0
            var i = offset
            for (w in 0 until lcnt) {
                v = h[w]
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
                v = v ushr 8
                out[i++] = v.toByte()
            }

            // basta?
            if (hashlen == Spec.max_digest_bytes) return

            // write the remaining bytes of a partial long value
            v = h[lcnt]
            i = lcnt shl 3
            while (i < hashlen) {
                out[offset + i] = v.toByte()
                v = v ushr 8
                ++i
            }
        }

        ////////////////////////////////////////////////////////////////////////
        /// Compression Kernel /////////////////////////////////////////// BEGIN
        ////////////////////////////////////////////////////////////////////////

        /** compress Spec.block_bytes data from b, from offset  */
        private fun compress(b: ByteArray, offset: Int) {

            // set m registers
            // REVU: some small gains still possible here.
            m[0] = b[offset].toLong() and 0xFF
            m[0] = m[0] or (b[offset + 1].toLong() and 0xFF shl 8)
            m[0] = m[0] or (b[offset + 2].toLong() and 0xFF shl 16)
            m[0] = m[0] or (b[offset + 3].toLong() and 0xFF shl 24)
            m[0] = m[0] or (b[offset + 4].toLong() and 0xFF shl 32)
            m[0] = m[0] or (b[offset + 5].toLong() and 0xFF shl 40)
            m[0] = m[0] or (b[offset + 6].toLong() and 0xFF shl 48)
            m[0] = m[0] or (b[offset + 7].toLong() shl 56)

            m[1] = b[offset + 8].toLong() and 0xFF
            m[1] = m[1] or (b[offset + 9].toLong() and 0xFF shl 8)
            m[1] = m[1] or (b[offset + 10].toLong() and 0xFF shl 16)
            m[1] = m[1] or (b[offset + 11].toLong() and 0xFF shl 24)
            m[1] = m[1] or (b[offset + 12].toLong() and 0xFF shl 32)
            m[1] = m[1] or (b[offset + 13].toLong() and 0xFF shl 40)
            m[1] = m[1] or (b[offset + 14].toLong() and 0xFF shl 48)
            m[1] = m[1] or (b[offset + 15].toLong() shl 56)

            m[2] = b[offset + 16].toLong() and 0xFF
            m[2] = m[2] or (b[offset + 17].toLong() and 0xFF shl 8)
            m[2] = m[2] or (b[offset + 18].toLong() and 0xFF shl 16)
            m[2] = m[2] or (b[offset + 19].toLong() and 0xFF shl 24)
            m[2] = m[2] or (b[offset + 20].toLong() and 0xFF shl 32)
            m[2] = m[2] or (b[offset + 21].toLong() and 0xFF shl 40)
            m[2] = m[2] or (b[offset + 22].toLong() and 0xFF shl 48)
            m[2] = m[2] or (b[offset + 23].toLong() shl 56)

            m[3] = b[offset + 24].toLong() and 0xFF
            m[3] = m[3] or (b[offset + 25].toLong() and 0xFF shl 8)
            m[3] = m[3] or (b[offset + 26].toLong() and 0xFF shl 16)
            m[3] = m[3] or (b[offset + 27].toLong() and 0xFF shl 24)
            m[3] = m[3] or (b[offset + 28].toLong() and 0xFF shl 32)
            m[3] = m[3] or (b[offset + 29].toLong() and 0xFF shl 40)
            m[3] = m[3] or (b[offset + 30].toLong() and 0xFF shl 48)
            m[3] = m[3] or (b[offset + 31].toLong() shl 56)

            m[4] = b[offset + 32].toLong() and 0xFF
            m[4] = m[4] or (b[offset + 33].toLong() and 0xFF shl 8)
            m[4] = m[4] or (b[offset + 34].toLong() and 0xFF shl 16)
            m[4] = m[4] or (b[offset + 35].toLong() and 0xFF shl 24)
            m[4] = m[4] or (b[offset + 36].toLong() and 0xFF shl 32)
            m[4] = m[4] or (b[offset + 37].toLong() and 0xFF shl 40)
            m[4] = m[4] or (b[offset + 38].toLong() and 0xFF shl 48)
            m[4] = m[4] or (b[offset + 39].toLong() shl 56)

            m[5] = b[offset + 40].toLong() and 0xFF
            m[5] = m[5] or (b[offset + 41].toLong() and 0xFF shl 8)
            m[5] = m[5] or (b[offset + 42].toLong() and 0xFF shl 16)
            m[5] = m[5] or (b[offset + 43].toLong() and 0xFF shl 24)
            m[5] = m[5] or (b[offset + 44].toLong() and 0xFF shl 32)
            m[5] = m[5] or (b[offset + 45].toLong() and 0xFF shl 40)
            m[5] = m[5] or (b[offset + 46].toLong() and 0xFF shl 48)
            m[5] = m[5] or (b[offset + 47].toLong() shl 56)

            m[6] = b[offset + 48].toLong() and 0xFF
            m[6] = m[6] or (b[offset + 49].toLong() and 0xFF shl 8)
            m[6] = m[6] or (b[offset + 50].toLong() and 0xFF shl 16)
            m[6] = m[6] or (b[offset + 51].toLong() and 0xFF shl 24)
            m[6] = m[6] or (b[offset + 52].toLong() and 0xFF shl 32)
            m[6] = m[6] or (b[offset + 53].toLong() and 0xFF shl 40)
            m[6] = m[6] or (b[offset + 54].toLong() and 0xFF shl 48)
            m[6] = m[6] or (b[offset + 55].toLong() shl 56)

            m[7] = b[offset + 56].toLong() and 0xFF
            m[7] = m[7] or (b[offset + 57].toLong() and 0xFF shl 8)
            m[7] = m[7] or (b[offset + 58].toLong() and 0xFF shl 16)
            m[7] = m[7] or (b[offset + 59].toLong() and 0xFF shl 24)
            m[7] = m[7] or (b[offset + 60].toLong() and 0xFF shl 32)
            m[7] = m[7] or (b[offset + 61].toLong() and 0xFF shl 40)
            m[7] = m[7] or (b[offset + 62].toLong() and 0xFF shl 48)
            m[7] = m[7] or (b[offset + 63].toLong() shl 56)

            m[8] = b[offset + 64].toLong() and 0xFF
            m[8] = m[8] or (b[offset + 65].toLong() and 0xFF shl 8)
            m[8] = m[8] or (b[offset + 66].toLong() and 0xFF shl 16)
            m[8] = m[8] or (b[offset + 67].toLong() and 0xFF shl 24)
            m[8] = m[8] or (b[offset + 68].toLong() and 0xFF shl 32)
            m[8] = m[8] or (b[offset + 69].toLong() and 0xFF shl 40)
            m[8] = m[8] or (b[offset + 70].toLong() and 0xFF shl 48)
            m[8] = m[8] or (b[offset + 71].toLong() shl 56)

            m[9] = b[offset + 72].toLong() and 0xFF
            m[9] = m[9] or (b[offset + 73].toLong() and 0xFF shl 8)
            m[9] = m[9] or (b[offset + 74].toLong() and 0xFF shl 16)
            m[9] = m[9] or (b[offset + 75].toLong() and 0xFF shl 24)
            m[9] = m[9] or (b[offset + 76].toLong() and 0xFF shl 32)
            m[9] = m[9] or (b[offset + 77].toLong() and 0xFF shl 40)
            m[9] = m[9] or (b[offset + 78].toLong() and 0xFF shl 48)
            m[9] = m[9] or (b[offset + 79].toLong() shl 56)

            m[10] = b[offset + 80].toLong() and 0xFF
            m[10] = m[10] or (b[offset + 81].toLong() and 0xFF shl 8)
            m[10] = m[10] or (b[offset + 82].toLong() and 0xFF shl 16)
            m[10] = m[10] or (b[offset + 83].toLong() and 0xFF shl 24)
            m[10] = m[10] or (b[offset + 84].toLong() and 0xFF shl 32)
            m[10] = m[10] or (b[offset + 85].toLong() and 0xFF shl 40)
            m[10] = m[10] or (b[offset + 86].toLong() and 0xFF shl 48)
            m[10] = m[10] or (b[offset + 87].toLong() shl 56)

            m[11] = b[offset + 88].toLong() and 0xFF
            m[11] = m[11] or (b[offset + 89].toLong() and 0xFF shl 8)
            m[11] = m[11] or (b[offset + 90].toLong() and 0xFF shl 16)
            m[11] = m[11] or (b[offset + 91].toLong() and 0xFF shl 24)
            m[11] = m[11] or (b[offset + 92].toLong() and 0xFF shl 32)
            m[11] = m[11] or (b[offset + 93].toLong() and 0xFF shl 40)
            m[11] = m[11] or (b[offset + 94].toLong() and 0xFF shl 48)
            m[11] = m[11] or (b[offset + 95].toLong() shl 56)

            m[12] = b[offset + 96].toLong() and 0xFF
            m[12] = m[12] or (b[offset + 97].toLong() and 0xFF shl 8)
            m[12] = m[12] or (b[offset + 98].toLong() and 0xFF shl 16)
            m[12] = m[12] or (b[offset + 99].toLong() and 0xFF shl 24)
            m[12] = m[12] or (b[offset + 100].toLong() and 0xFF shl 32)
            m[12] = m[12] or (b[offset + 101].toLong() and 0xFF shl 40)
            m[12] = m[12] or (b[offset + 102].toLong() and 0xFF shl 48)
            m[12] = m[12] or (b[offset + 103].toLong() shl 56)

            m[13] = b[offset + 104].toLong() and 0xFF
            m[13] = m[13] or (b[offset + 105].toLong() and 0xFF shl 8)
            m[13] = m[13] or (b[offset + 106].toLong() and 0xFF shl 16)
            m[13] = m[13] or (b[offset + 107].toLong() and 0xFF shl 24)
            m[13] = m[13] or (b[offset + 108].toLong() and 0xFF shl 32)
            m[13] = m[13] or (b[offset + 109].toLong() and 0xFF shl 40)
            m[13] = m[13] or (b[offset + 110].toLong() and 0xFF shl 48)
            m[13] = m[13] or (b[offset + 111].toLong() shl 56)

            m[14] = b[offset + 112].toLong() and 0xFF
            m[14] = m[14] or (b[offset + 113].toLong() and 0xFF shl 8)
            m[14] = m[14] or (b[offset + 114].toLong() and 0xFF shl 16)
            m[14] = m[14] or (b[offset + 115].toLong() and 0xFF shl 24)
            m[14] = m[14] or (b[offset + 116].toLong() and 0xFF shl 32)
            m[14] = m[14] or (b[offset + 117].toLong() and 0xFF shl 40)
            m[14] = m[14] or (b[offset + 118].toLong() and 0xFF shl 48)
            m[14] = m[14] or (b[offset + 119].toLong() shl 56)

            m[15] = b[offset + 120].toLong() and 0xFF
            m[15] = m[15] or (b[offset + 121].toLong() and 0xFF shl 8)
            m[15] = m[15] or (b[offset + 122].toLong() and 0xFF shl 16)
            m[15] = m[15] or (b[offset + 123].toLong() and 0xFF shl 24)
            m[15] = m[15] or (b[offset + 124].toLong() and 0xFF shl 32)
            m[15] = m[15] or (b[offset + 125].toLong() and 0xFF shl 40)
            m[15] = m[15] or (b[offset + 126].toLong() and 0xFF shl 48)
            m[15] = m[15] or (b[offset + 127].toLong() shl 56)
            //			Debug.dumpArray("m @ compress", m);
            //
            //			Debug.dumpArray("h @ compress", h);
            //			Debug.dumpArray("t @ compress", t);
            //			Debug.dumpArray("f @ compress", f);

            // set v registers
            v[0] = h[0]
            v[1] = h[1]
            v[2] = h[2]
            v[3] = h[3]
            v[4] = h[4]
            v[5] = h[5]
            v[6] = h[6]
            v[7] = h[7]
            v[8] = 0x6a09e667f3bcc908L
            v[9] = -0x4498517a7b3558c5L
            v[10] = 0x3c6ef372fe94f82bL
            v[11] = -0x5ab00ac5a0e2c90fL
            v[12] = t[0] xor 0x510e527fade682d1L
            v[13] = t[1] xor -0x64fa9773d4c193e1L
            v[14] = f[0] xor 0x1f83d9abfb41bd6bL
            v[15] = f[1] xor 0x5be0cd19137e2179L

            //			Debug.dumpArray("v @ compress", v);
            // the rounds
            // REVU: let's try unrolling this again TODO do & bench
            for (r in 0..11) {

                /**		G (r, 0, 0, 4,  8, 12);  */

                v[0] = v[0] + v[4] + m[sig_g00[r]]
                v[12] = v[12] xor v[0]
                v[12] = v[12] shl 32 or v[12].ushr(32)
                v[8] = v[8] + v[12]
                v[4] = v[4] xor v[8]
                v[4] = v[4].ushr(24) or (v[4] shl 40)
                v[0] = v[0] + v[4] + m[sig_g01[r]]
                v[12] = v[12] xor v[0]
                v[12] = v[12].ushr(16) or (v[12] shl 48)
                v[8] = v[8] + v[12]
                v[4] = v[4] xor v[8]
                v[4] = v[4] shl 1 or v[4].ushr(63)

                /**		G (r, 1, 1, 5,  9, 13);  */

                v[1] = v[1] + v[5] + m[sig_g10[r]]
                v[13] = v[13] xor v[1]
                v[13] = v[13] shl 32 or v[13].ushr(32)
                v[9] = v[9] + v[13]
                v[5] = v[5] xor v[9]
                v[5] = v[5].ushr(24) or (v[5] shl 40)
                v[1] = v[1] + v[5] + m[sig_g11[r]]
                v[13] = v[13] xor v[1]
                v[13] = v[13].ushr(16) or (v[13] shl 48)
                v[9] = v[9] + v[13]
                v[5] = v[5] xor v[9]
                v[5] = v[5] shl 1 or v[5].ushr(63)

                /**		G (r, 2, 2, 6, 10, 14);  */

                v[2] = v[2] + v[6] + m[sig_g20[r]]
                v[14] = v[14] xor v[2]
                v[14] = v[14] shl 32 or v[14].ushr(32)
                v[10] = v[10] + v[14]
                v[6] = v[6] xor v[10]
                v[6] = v[6].ushr(24) or (v[6] shl 40)
                v[2] = v[2] + v[6] + m[sig_g21[r]]
                v[14] = v[14] xor v[2]
                v[14] = v[14].ushr(16) or (v[14] shl 48)
                v[10] = v[10] + v[14]
                v[6] = v[6] xor v[10]
                v[6] = v[6] shl 1 or v[6].ushr(63)

                /**		G (r, 3, 3, 7, 11, 15);  */

                v[3] = v[3] + v[7] + m[sig_g30[r]]
                v[15] = v[15] xor v[3]
                v[15] = v[15] shl 32 or v[15].ushr(32)
                v[11] = v[11] + v[15]
                v[7] = v[7] xor v[11]
                v[7] = v[7].ushr(24) or (v[7] shl 40)
                v[3] = v[3] + v[7] + m[sig_g31[r]]
                v[15] = v[15] xor v[3]
                v[15] = v[15].ushr(16) or (v[15] shl 48)
                v[11] = v[11] + v[15]
                v[7] = v[7] xor v[11]
                v[7] = v[7] shl 1 or v[7].ushr(63)

                /**		G (r, 4, 0, 5, 10, 15);  */

                v[0] = v[0] + v[5] + m[sig_g40[r]]
                v[15] = v[15] xor v[0]
                v[15] = v[15] shl 32 or v[15].ushr(32)
                v[10] = v[10] + v[15]
                v[5] = v[5] xor v[10]
                v[5] = v[5].ushr(24) or (v[5] shl 40)
                v[0] = v[0] + v[5] + m[sig_g41[r]]
                v[15] = v[15] xor v[0]
                v[15] = v[15].ushr(16) or (v[15] shl 48)
                v[10] = v[10] + v[15]
                v[5] = v[5] xor v[10]
                v[5] = v[5] shl 1 or v[5].ushr(63)

                /**		G (r, 5, 1, 6, 11, 12);  */

                v[1] = v[1] + v[6] + m[sig_g50[r]]
                v[12] = v[12] xor v[1]
                v[12] = v[12] shl 32 or v[12].ushr(32)
                v[11] = v[11] + v[12]
                v[6] = v[6] xor v[11]
                v[6] = v[6].ushr(24) or (v[6] shl 40)
                v[1] = v[1] + v[6] + +m[sig_g51[r]]
                v[12] = v[12] xor v[1]
                v[12] = v[12].ushr(16) or (v[12] shl 48)
                v[11] = v[11] + v[12]
                v[6] = v[6] xor v[11]
                v[6] = v[6] shl 1 or v[6].ushr(63)

                /**		G (r, 6, 2, 7,  8, 13);  */

                v[2] = v[2] + v[7] + m[sig_g60[r]]
                v[13] = v[13] xor v[2]
                v[13] = v[13] shl 32 or v[13].ushr(32)
                v[8] = v[8] + v[13]
                v[7] = v[7] xor v[8]
                v[7] = v[7].ushr(24) or (v[7] shl 40)
                v[2] = v[2] + v[7] + m[sig_g61[r]]
                v[13] = v[13] xor v[2]
                v[13] = v[13].ushr(16) or (v[13] shl 48)
                v[8] = v[8] + v[13]
                v[7] = v[7] xor v[8]
                v[7] = v[7] shl 1 or v[7].ushr(63)

                /**		G (r, 7, 3, 4,  9, 14);  */

                v[3] = v[3] + v[4] + m[sig_g70[r]]
                v[14] = v[14] xor v[3]
                v[14] = v[14] shl 32 or v[14].ushr(32)
                v[9] = v[9] + v[14]
                v[4] = v[4] xor v[9]
                v[4] = v[4].ushr(24) or (v[4] shl 40)
                v[3] = v[3] + v[4] + m[sig_g71[r]]
                v[14] = v[14] xor v[3]
                v[14] = v[14].ushr(16) or (v[14] shl 48)
                v[9] = v[9] + v[14]
                v[4] = v[4] xor v[9]
                v[4] = v[4] shl 1 or v[4].ushr(63)
            }

            // Update state vector h
            h[0] = h[0] xor (v[0] xor v[8])
            h[1] = h[1] xor (v[1] xor v[9])
            h[2] = h[2] xor (v[2] xor v[10])
            h[3] = h[3] xor (v[3] xor v[11])
            h[4] = h[4] xor (v[4] xor v[12])
            h[5] = h[5] xor (v[5] xor v[13])
            h[6] = h[6] xor (v[6] xor v[14])
            h[7] = h[7] xor (v[7] xor v[15])

            //			Debug.dumpArray("v @ compress end", v);
            //			Debug.dumpArray("h @ compress end", h);
            /* kaamil */
        }

        ////////////////////////////////////////////////////////////////////////
        /// Compression Kernel //////////////////////////////////////////// FINI
        ////////////////////////////////////////////////////////////////////////

        /* TEMP - remove at will */
        object Debug {
            fun dumpState(e: Blake2b.Engine, mark: String) {
                System.out.format("-- MARK == @ %s @ ===========\n", mark)
                dumpArray("register t", e.t)
                dumpArray("register h", e.h)
                dumpArray("register f", e.f)
                dumpArray("register offset", longArrayOf(e.buflen.toLong()))
                System.out.format("-- END MARK =================\n")
            }

            fun dumpArray(label: String, b: LongArray) {
                System.out.format("-- %s -- :\n{\n", label)
                for (j in b.indices) {
                    System.out.format("    [%2d] : %016X\n", j, b[j])
                }
                System.out.format("}\n")
            }

            fun dumpBuffer(out: PrintStream, b: ByteArray) {
                dumpBuffer(out, null, b, 0, b.size)
            }

            fun dumpBuffer(out: PrintStream, b: ByteArray, offset: Int, len: Int) {
                dumpBuffer(out, null, b, offset, len)
            }

            @JvmOverloads
            fun dumpBuffer(out: PrintStream, label: String?, b: ByteArray, offset: Int = 0, len: Int = b.size) {
                if (label != null)
                    out.format("-- %s -- :\n", label)
                out.format("{\n    ", label)
                for (j in 0 until len) {
                    out.format("%02X", b[j + offset])
                    if (j + 1 < len) {
                        if ((j + 1) % 8 == 0)
                            out.print("\n    ")
                        else
                            out.print(' ')
                    }
                }
                out.format("\n}\n")
            }
        }
        /* TEMP - remove at will */

        // ---------------------------------------------------------------------
        // Helper for assert error messages
        // ---------------------------------------------------------------------
        object Assert {
            val exclusiveUpperBound = "'%s' %d is >= %d"
            val inclusiveUpperBound = "'%s' %d is > %d"
            val exclusiveLowerBound = "'%s' %d is <= %d"
            val inclusiveLowerBound = "'%s' %d is < %d"
            internal fun <T : Number> assertFail(name: String, v: T, err: String, spec: T): String {
                Exception().printStackTrace()
                return String.format(err, name, v, spec)
            }
        }
        // ---------------------------------------------------------------------
        // Little Endian Codecs (inlined in the compressor)
        /*
		 * impl note: these are not library funcs and used in hot loops, so no
		 * null or bounds checks are performed. For our purposes, this is OK.
		 */
        // ---------------------------------------------------------------------

        object LittleEndian {
            private val hex_digits = byteArrayOf(
                '0'.toByte(),
                '1'.toByte(),
                '2'.toByte(),
                '3'.toByte(),
                '4'.toByte(),
                '5'.toByte(),
                '6'.toByte(),
                '7'.toByte(),
                '8'.toByte(),
                '9'.toByte(),
                'a'.toByte(),
                'b'.toByte(),
                'c'.toByte(),
                'd'.toByte(),
                'e'.toByte(),
                'f'.toByte()
            )
            private val HEX_digits = byteArrayOf(
                '0'.toByte(),
                '1'.toByte(),
                '2'.toByte(),
                '3'.toByte(),
                '4'.toByte(),
                '5'.toByte(),
                '6'.toByte(),
                '7'.toByte(),
                '8'.toByte(),
                '9'.toByte(),
                'A'.toByte(),
                'B'.toByte(),
                'C'.toByte(),
                'D'.toByte(),
                'E'.toByte(),
                'F'.toByte()
            )

            @JvmOverloads
            fun toHexStr(b: ByteArray, upperCase: Boolean = false): String {
                val len = b.size
                val digits = ByteArray(len * 2)
                val hexRep = if (upperCase) HEX_digits else hex_digits
                for (i in 0 until len) {

                    val digitsIndex = b[i].toInt() shr 4 and 0x0F
                    digits[i * 2] = hexRep[digitsIndex]

                    val digitsIndex2 = b[i].toInt() and 0x0F
                    digits[i * 2 + 1] = hexRep[digitsIndex2]
                }
                return String(digits)
            }

            fun readInt(b: ByteArray, off: Int): Int {
                var off = off
                var v0 = b[off++].toInt() and 0xFF
                v0 = v0 or (b[off++].toInt() and 0xFF shl 8)
                v0 = v0 or (b[off++].toInt() and 0xFF shl 16)
                v0 = v0 or (b[off].toInt() shl 24)
                return v0
            }

            /** Little endian - byte[] to long  */
            fun readLong(b: ByteArray, off: Int): Long {
                var off = off
                var v0 = b[off++].toLong() and 0xFF
                v0 = v0 or (b[off++].toLong() and 0xFF shl 8)
                v0 = v0 or (b[off++].toLong() and 0xFF shl 16)
                v0 = v0 or (b[off++].toLong() and 0xFF shl 24)
                v0 = v0 or (b[off++].toLong() and 0xFF shl 32)
                v0 = v0 or (b[off++].toLong() and 0xFF shl 40)
                v0 = v0 or (b[off++].toLong() and 0xFF shl 48)
                v0 = v0 or (b[off].toLong() shl 56)
                return v0
            }
            /**  */
            /** Little endian - long to byte[]  */
            fun writeLong(v: Long, b: ByteArray, off: Int) {
                var v = v
                b[off] = v.toByte()
                v = v ushr 8
                b[off + 1] = v.toByte()
                v = v ushr 8
                b[off + 2] = v.toByte()
                v = v ushr 8
                b[off + 3] = v.toByte()
                v = v ushr 8
                b[off + 4] = v.toByte()
                v = v ushr 8
                b[off + 5] = v.toByte()
                v = v ushr 8
                b[off + 6] = v.toByte()
                v = v ushr 8
                b[off + 7] = v.toByte()
            }

            /** Little endian - int to byte[]  */
            fun writeInt(v: Int, b: ByteArray, off: Int) {
                var v = v
                b[off] = v.toByte()
                v = v ushr 8
                b[off + 1] = v.toByte()
                v = v ushr 8
                b[off + 2] = v.toByte()
                v = v ushr 8
                b[off + 3] = v.toByte()
            }
        }

        /** @return hex rep of byte (lower case).
         */// because String class is slower.

        companion object {

            /* G0 sigmas */
            internal val sig_g00 = intArrayOf(0, 14, 11, 7, 9, 2, 12, 13, 6, 10, 0, 14)
            internal val sig_g01 = intArrayOf(1, 10, 8, 9, 0, 12, 5, 11, 15, 2, 1, 10)

            /* G1 sigmas */
            internal val sig_g10 = intArrayOf(2, 4, 12, 3, 5, 6, 1, 7, 14, 8, 2, 4)
            internal val sig_g11 = intArrayOf(3, 8, 0, 1, 7, 10, 15, 14, 9, 4, 3, 8)

            /* G2 sigmas */
            internal val sig_g20 = intArrayOf(4, 9, 5, 13, 2, 0, 14, 12, 11, 7, 4, 9)
            internal val sig_g21 = intArrayOf(5, 15, 2, 12, 4, 11, 13, 1, 3, 6, 5, 15)

            /* G3 sigmas */
            internal val sig_g30 = intArrayOf(6, 13, 15, 11, 10, 8, 4, 3, 0, 1, 6, 13)
            internal val sig_g31 = intArrayOf(7, 6, 13, 14, 15, 3, 10, 9, 8, 5, 7, 6)

            /* G4 sigmas */
            internal val sig_g40 = intArrayOf(8, 1, 10, 2, 14, 4, 0, 5, 12, 15, 8, 1)
            internal val sig_g41 = intArrayOf(9, 12, 14, 6, 1, 13, 7, 0, 2, 11, 9, 12)

            /* G5 sigmas */
            internal val sig_g50 = intArrayOf(10, 0, 3, 5, 11, 7, 6, 15, 13, 9, 10, 0)
            internal val sig_g51 = intArrayOf(11, 2, 6, 10, 12, 5, 3, 4, 7, 14, 11, 2)

            /* G6 sigmas */
            internal val sig_g60 = intArrayOf(12, 11, 7, 4, 6, 15, 9, 8, 1, 3, 12, 11)
            internal val sig_g61 = intArrayOf(13, 7, 1, 0, 8, 14, 2, 6, 4, 12, 13, 7)

            /* G7 sigmas */
            internal val sig_g70 = intArrayOf(14, 5, 9, 15, 3, 1, 8, 2, 10, 13, 14, 5)
            internal val sig_g71 = intArrayOf(15, 3, 4, 8, 13, 9, 11, 10, 5, 0, 15, 3)

            /** read only  */
            private val zeropad = ByteArray(Spec.block_bytes)

            @JvmStatic
            fun main(args: Array<String>) {
                val mac = Mac.newInstance("LOVE".toByteArray())
                val hash = mac.digest("Salaam!".toByteArray())
            }
        }
    }
    // ---------------------------------------------------------------------
    // Ctor & Initialization
    // ---------------------------------------------------------------------
    /** Basic use constructor pending (TODO) JCA/JCE compliance  */
    // ---------------------------------------------------------------------
    // digest parameter (block)
    // ---------------------------------------------------------------------
    /** Blake2b configuration parameters block per spec  */
    // REVU: need to review a revert back to non-lazy impl TODO: do & bench
    class Param : AlgorithmParameterSpec {

        /**  */
        var hasKey = false
        /** not sure how to make this secure - TODO  */
        var key_bytes: ByteArray? = null
        /**  */
        private var bytes: ByteArray? = null
        /**  */
        private val h = LongArray(Spec.state_space_len)
        // TODO same for tree params depth, fanout, inner, node-depth, node-offset
        val digestLength: Int
            get() = getByteParam(Xoff.digest_length).toInt()
        val keyLength: Int
            get() = getByteParam(Xoff.key_length).toInt()
        val fanout: Int
            get() = getByteParam(Xoff.fanout).toInt()
        val depth: Int
            get() = getByteParam(Xoff.depth).toInt()
        val leafLength: Int
            get() = getIntParam(Xoff.leaf_length)
        val nodeOffset: Long
            get() = getLongParam(Xoff.node_offset)
        val nodeDepth: Int
            get() = getByteParam(Xoff.node_depth).toInt()
        val innerLength: Int
            get() = getByteParam(Xoff.inner_length).toInt()

        interface Xoff {
            companion object {
                val digest_length = 0
                val key_length = 1
                val fanout = 2
                val depth = 3
                val leaf_length = 4
                val node_offset = 8
                val node_depth = 16
                val inner_length = 17
                val reserved = 18
                val salt = 32
                val personal = 48
            }
        }

        interface Default {
            companion object {
                val digest_length = Spec.max_digest_bytes.toByte()
                val key_length: Byte = 0
                val fanout: Byte = 1
                val depth: Byte = 1
                val leaf_length = 0
                val node_offset: Long = 0
                val node_depth: Byte = 0
                val inner_length: Byte = 0
            }
        }

        /**  */
        init {
            System.arraycopy(default_h, 0, h, 0, Spec.state_space_len)
        }

        /**  */
        fun initialized_H(): LongArray {
            return h
        }

        /** package only - copy returned - do not use in functional loops  */
        fun getBytes(): ByteArray {
            lazyInitBytes()
            val copy = ByteArray(bytes!!.size)
            System.arraycopy(bytes!!, 0, copy, 0, bytes!!.size)
            return copy
        }

        internal fun getByteParam(xoffset: Int): Byte {
            var _bytes = bytes
            if (_bytes == null) _bytes = default_bytes
            return _bytes[xoffset]
        }

        internal fun getIntParam(xoffset: Int): Int {
            var _bytes = bytes
            if (_bytes == null) _bytes = default_bytes
            return Engine.LittleEndian.readInt(_bytes, xoffset)
        }

        internal fun getLongParam(xoffset: Int): Long {
            var _bytes = bytes
            if (_bytes == null) _bytes = default_bytes
            return Engine.LittleEndian.readLong(_bytes, xoffset)
        }

        fun hasKey(): Boolean {
            return this.hasKey
        }

        fun clone(): Param {
            val clone = Param()
            System.arraycopy(this.h, 0, clone.h, 0, h.size)
            clone.lazyInitBytes()
            System.arraycopy(this.bytes!!, 0, clone.bytes!!, 0, this.bytes!!.size)

            if (this.hasKey) {
                clone.hasKey = this.hasKey
                clone.key_bytes = ByteArray(Spec.max_key_bytes * 2)
                System.arraycopy(this.key_bytes!!, 0, clone.key_bytes!!, 0, this.key_bytes!!.size)
            }
            return clone
        }

        ////////////////////////////////////////////////////////////////////////
        /// lazy setters - write directly to the bytes image of param block ////
        ////////////////////////////////////////////////////////////////////////
        private fun lazyInitBytes() {
            if (bytes == null) {
                bytes = ByteArray(Spec.param_bytes)
                System.arraycopy(default_bytes, 0, bytes!!, 0, Spec.param_bytes)
            }
        }

        /* 0-7 inclusive */
        fun setDigestLength(len: Int): Param {
            assert(len > 0) { Engine.Assert.assertFail("len", len, Engine.Assert.exclusiveLowerBound, 0) }
            assert(len <= Spec.max_digest_bytes) {
                Engine.Assert.assertFail(
                    "len",
                    len,
                    Engine.Assert.inclusiveUpperBound,
                    Spec.max_digest_bytes
                )
            }

            lazyInitBytes()
            bytes?.set(Xoff.digest_length, len.toByte())
            h[0] = Engine.LittleEndian.readLong(bytes!!, 0)
            h[0] = h[0] xor Spec.IV[0]
            return this
        }

        fun setKey(key: Key?): Param {
            assert(key != null) { "key is null" }
            val keyBytes = key!!.encoded ?: error("key.encoded() is null")
            return this.setKey(keyBytes)
        }

        fun setKey(key: ByteArray?): Param {
            assert(key != null) { "key is null" }
            assert(key!!.size >= 0) {
                Engine.Assert.assertFail(
                    "key.length", key.size, Engine.Assert.inclusiveUpperBound, 0
                )
            }
            assert(key.size <= Spec.max_key_bytes) {
                Engine.Assert.assertFail(
                    "key.length", key.size, Engine.Assert.inclusiveUpperBound, Spec.max_key_bytes
                )
            }

            // zeropad keybytes
            this.key_bytes = ByteArray(Spec.max_key_bytes * 2)
            System.arraycopy(key, 0, this.key_bytes!!, 0, key.size)
            lazyInitBytes()
            bytes?.set(Xoff.key_length, key.size.toByte()) // checked c ref; this is correct
            h[0] = Engine.LittleEndian.readLong(bytes!!, 0)
            h[0] = h[0] xor Spec.IV[0]
            this.hasKey = true
            return this
        }

        fun setFanout(fanout: Int): Param {
            assert(fanout > 0) {
                Engine.Assert.assertFail(
                    "fanout", fanout, Engine.Assert.exclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            bytes?.set(Xoff.fanout, fanout.toByte())
            h[0] = Engine.LittleEndian.readLong(bytes!!, 0)
            h[0] = h[0] xor Spec.IV[0]
            return this
        }

        fun setDepth(depth: Int): Param {
            assert(depth > 0) {
                Engine.Assert.assertFail(
                    "depth", depth, Engine.Assert.exclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            bytes?.set(Xoff.depth, depth.toByte())
            h[0] = Engine.LittleEndian.readLong(bytes!!, 0)
            h[0] = h[0] xor Spec.IV[0]
            return this
        }

        fun setLeafLength(leaf_length: Int): Param {
            assert(leaf_length >= 0) {
                Engine.Assert.assertFail(
                    "leaf_length", leaf_length, Engine.Assert.inclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            Engine.LittleEndian.writeInt(leaf_length, bytes!!, Xoff.leaf_length)
            h[0] = Engine.LittleEndian.readLong(bytes!!, 0)
            h[0] = h[0] xor Spec.IV[0]
            return this
        }

        /* 8-15 inclusive */
        fun setNodeOffset(node_offset: Long): Param {
            assert(node_offset >= 0) {
                Engine.Assert.assertFail<Number>(
                    "node_offset", node_offset, Engine.Assert.inclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            Engine.LittleEndian.writeLong(node_offset, bytes!!, Xoff.node_offset)
            h[1] = Engine.LittleEndian.readLong(bytes!!, Xoff.node_offset)
            h[1] = h[1] xor Spec.IV[1]
            return this
        }

        /* 16-23 inclusive */
        fun setNodeDepth(node_depth: Int): Param {
            assert(node_depth >= 0) {
                Engine.Assert.assertFail(
                    "node_depth", node_depth, Engine.Assert.inclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            if (bytes != null) {
                bytes!![Xoff.node_depth] = node_depth.toByte()
            }
            h[2] = Engine.LittleEndian.readLong(bytes!!, Xoff.node_depth)
            h[2] = h[2] xor Spec.IV[2]
            h[3] = Engine.LittleEndian.readLong(bytes!!, Xoff.node_depth + 8)
            h[3] = h[3] xor Spec.IV[3]
            return this
        }

        fun setInnerLength(inner_length: Int): Param {
            assert(inner_length >= 0) {
                Engine.Assert.assertFail(
                    "inner_length", inner_length, Engine.Assert.inclusiveLowerBound, 0
                )
            }

            lazyInitBytes()
            if (bytes != null) {
                bytes!![Xoff.inner_length] = inner_length.toByte()
            }
            h[2] = Engine.LittleEndian.readLong(bytes!!, Xoff.node_depth)
            h[2] = h[2] xor Spec.IV[2]
            h[3] = Engine.LittleEndian.readLong(bytes!!, Xoff.node_depth + 8)
            h[3] = h[3] xor Spec.IV[3]
            return this
        }

        /* 24-31 masked by reserved and remain unchanged */

        /* 32-47 inclusive */
        fun setSalt(salt: ByteArray?): Param {
            assert(salt != null) { "salt is null" }
            assert(salt!!.size <= Spec.max_salt_bytes) {
                Engine.Assert.assertFail(
                    "salt.length", salt.size, Engine.Assert.inclusiveUpperBound, Spec.max_salt_bytes
                )
            }

            lazyInitBytes()
            Arrays.fill(bytes!!, Xoff.salt, Xoff.salt + Spec.max_salt_bytes, 0.toByte())
            System.arraycopy(salt, 0, bytes!!, Xoff.salt, salt.size)
            h[4] = Engine.LittleEndian.readLong(bytes!!, Xoff.salt)
            h[4] = h[4] xor Spec.IV[4]
            h[5] = Engine.LittleEndian.readLong(bytes!!, Xoff.salt + 8)
            h[5] = h[5] xor Spec.IV[5]
            return this
        }

        /* 48-63 inclusive */
        fun setPersonal(personal: ByteArray?): Param {
            assert(personal != null) { "personal is null" }
            assert(personal!!.size <= Spec.max_personalization_bytes) {
                Engine.Assert.assertFail(
                    "personal.length",
                    personal.size,
                    Engine.Assert.inclusiveUpperBound,
                    Spec.max_personalization_bytes
                )
            }

            lazyInitBytes()
            Arrays.fill(bytes!!, Xoff.personal, Xoff.personal + Spec.max_personalization_bytes, 0.toByte())
            System.arraycopy(personal, 0, bytes!!, Xoff.personal, personal.size)
            h[6] = Engine.LittleEndian.readLong(bytes!!, Xoff.personal)
            h[6] = h[6] xor Spec.IV[6]
            h[7] = Engine.LittleEndian.readLong(bytes!!, Xoff.personal + 8)
            h[7] = h[7] xor Spec.IV[7]
            return this
        }

        companion object {
            /** default bytes of Blake2b parameter block  */
            internal val default_bytes = ByteArray(Spec.param_bytes)

            /** initialize default_bytes  */
            init {
                default_bytes[Xoff.digest_length] = Default.digest_length
                default_bytes[Xoff.key_length] = Default.key_length
                default_bytes[Xoff.fanout] = Default.fanout
                default_bytes[Xoff.depth] = Default.depth
                /* def. leaf_length is 0 fill and already set by new byte[] */
                /* def. node_offset is 0 fill and already set by new byte[] */
                default_bytes[Xoff.node_depth] = Default.node_depth
                default_bytes[Xoff.inner_length] = Default.inner_length
                /* def. salt is 0 fill and already set by new byte[] */
                /* def. personal is 0 fill and already set by new byte[] */
            }

            /** default Blake2b h vector  */
            internal val default_h = LongArray(Spec.state_space_len)

            init {
                default_h[0] = Engine.LittleEndian.readLong(default_bytes, 0)
                default_h[1] = Engine.LittleEndian.readLong(default_bytes, 8)
                default_h[2] = Engine.LittleEndian.readLong(default_bytes, 16)
                default_h[3] = Engine.LittleEndian.readLong(default_bytes, 24)
                default_h[4] = Engine.LittleEndian.readLong(default_bytes, 32)
                default_h[5] = Engine.LittleEndian.readLong(default_bytes, 40)
                default_h[6] = Engine.LittleEndian.readLong(default_bytes, 48)
                default_h[7] = Engine.LittleEndian.readLong(default_bytes, 56)

                default_h[0] = default_h[0] xor Spec.IV[0]
                default_h[1] = default_h[1] xor Spec.IV[1]
                default_h[2] = default_h[2] xor Spec.IV[2]
                default_h[3] = default_h[3] xor Spec.IV[3]
                default_h[4] = default_h[4] xor Spec.IV[4]
                default_h[5] = default_h[5] xor Spec.IV[5]
                default_h[6] = default_h[6] xor Spec.IV[6]
                default_h[7] = default_h[7] xor Spec.IV[7]
            }
        }
        ////////////////////////////////////////////////////////////////////////
        /// lazy setters /////////////////////////////////////////////////// END
        ////////////////////////////////////////////////////////////////////////
    }
}