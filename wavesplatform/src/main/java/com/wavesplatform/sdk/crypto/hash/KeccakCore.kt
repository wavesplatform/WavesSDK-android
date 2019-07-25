// $Id: KeccakCore.java 258 2011-07-15 22:16:50Z tp $

package com.wavesplatform.sdk.crypto.hash

/**
 * This class implements the core operations for the Keccak digest
 * algorithm.
 *
 * <pre>
 * ==========================(LICENSE BEGIN)============================
 *
 * Copyright (c) 2007-2010  Projet RNRT SAPHIR
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * ===========================(LICENSE END)=============================
</pre> *
 *
 * @version   $Revision: 258 $
 * @author    Thomas Pornin &lt;thomas.pornin@cryptolog.com&gt;
 */

abstract class KeccakCore : DigestEngine() {

    private var A: LongArray? = null
    private var tmpOut: ByteArray? = null

    /** @see Digest
     */
    override val blockLength: Int
        get() = 200 - 2 * digestLength

    /** @see DigestEngine
     */
    override fun engineReset() {
        doReset()
    }

    /** @see DigestEngine
     */
    override fun processBlock(data: ByteArray) {
        /* Input block */
        var i = 0
        while (i < data.size) {
            A!![i.ushr(3)] = A!![i.ushr(3)] xor decodeLELong(data, i)
            i += 8
        }

        var t0: Long
        var t1: Long
        var t2: Long
        var t3: Long
        var t4: Long
        var tt0: Long
        var tt1: Long
        var tt2: Long
        var tt3: Long
        val tt4: Long
        var t: Long
        var kt: Long
        var c0: Long
        var c1: Long
        var c2: Long
        var c3: Long
        var c4: Long
        var bnn: Long

        /*
		 * Unrolling four rounds kills performance big time
		 * on Intel x86 Core2, in both 32-bit and 64-bit modes
		 * (less than 1 MB/s instead of 55 MB/s on x86-64).
		 * Unrolling two rounds appears to be fine.
		 */
        var j = 0
        while (j < 24) {

            tt0 = A!![1] xor A!![6]
            tt1 = A!![11] xor A!![16]
            tt0 = tt0 xor (A!![21] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![4] xor A!![9]
            tt3 = A!![14] xor A!![19]
            tt0 = tt0 xor A!![24]
            tt2 = tt2 xor tt3
            t0 = tt0 xor tt2

            tt0 = A!![2] xor A!![7]
            tt1 = A!![12] xor A!![17]
            tt0 = tt0 xor (A!![22] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![0] xor A!![5]
            tt3 = A!![10] xor A!![15]
            tt0 = tt0 xor A!![20]
            tt2 = tt2 xor tt3
            t1 = tt0 xor tt2

            tt0 = A!![3] xor A!![8]
            tt1 = A!![13] xor A!![18]
            tt0 = tt0 xor (A!![23] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![1] xor A!![6]
            tt3 = A!![11] xor A!![16]
            tt0 = tt0 xor A!![21]
            tt2 = tt2 xor tt3
            t2 = tt0 xor tt2

            tt0 = A!![4] xor A!![9]
            tt1 = A!![14] xor A!![19]
            tt0 = tt0 xor (A!![24] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![2] xor A!![7]
            tt3 = A!![12] xor A!![17]
            tt0 = tt0 xor A!![22]
            tt2 = tt2 xor tt3
            t3 = tt0 xor tt2

            tt0 = A!![0] xor A!![5]
            tt1 = A!![10] xor A!![15]
            tt0 = tt0 xor (A!![20] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![3] xor A!![8]
            tt3 = A!![13] xor A!![18]
            tt0 = tt0 xor A!![23]
            tt2 = tt2 xor tt3
            t4 = tt0 xor tt2

            A!![0] = A!![0] xor t0
            A!![5] = A!![5] xor t0
            A!![10] = A!![10] xor t0
            A!![15] = A!![15] xor t0
            A!![20] = A!![20] xor t0
            A!![1] = A!![1] xor t1
            A!![6] = A!![6] xor t1
            A!![11] = A!![11] xor t1
            A!![16] = A!![16] xor t1
            A!![21] = A!![21] xor t1
            A!![2] = A!![2] xor t2
            A!![7] = A!![7] xor t2
            A!![12] = A!![12] xor t2
            A!![17] = A!![17] xor t2
            A!![22] = A!![22] xor t2
            A!![3] = A!![3] xor t3
            A!![8] = A!![8] xor t3
            A!![13] = A!![13] xor t3
            A!![18] = A!![18] xor t3
            A!![23] = A!![23] xor t3
            A!![4] = A!![4] xor t4
            A!![9] = A!![9] xor t4
            A!![14] = A!![14] xor t4
            A!![19] = A!![19] xor t4
            A!![24] = A!![24] xor t4
            A!![5] = A!![5] shl 36 or A!![5].ushr(64 - 36)
            A!![10] = A!![10] shl 3 or A!![10].ushr(64 - 3)
            A!![15] = A!![15] shl 41 or A!![15].ushr(64 - 41)
            A!![20] = A!![20] shl 18 or A!![20].ushr(64 - 18)
            A!![1] = A!![1] shl 1 or A!![1].ushr(64 - 1)
            A!![6] = A!![6] shl 44 or A!![6].ushr(64 - 44)
            A!![11] = A!![11] shl 10 or A!![11].ushr(64 - 10)
            A!![16] = A!![16] shl 45 or A!![16].ushr(64 - 45)
            A!![21] = A!![21] shl 2 or A!![21].ushr(64 - 2)
            A!![2] = A!![2] shl 62 or A!![2].ushr(64 - 62)
            A!![7] = A!![7] shl 6 or A!![7].ushr(64 - 6)
            A!![12] = A!![12] shl 43 or A!![12].ushr(64 - 43)
            A!![17] = A!![17] shl 15 or A!![17].ushr(64 - 15)
            A!![22] = A!![22] shl 61 or A!![22].ushr(64 - 61)
            A!![3] = A!![3] shl 28 or A!![3].ushr(64 - 28)
            A!![8] = A!![8] shl 55 or A!![8].ushr(64 - 55)
            A!![13] = A!![13] shl 25 or A!![13].ushr(64 - 25)
            A!![18] = A!![18] shl 21 or A!![18].ushr(64 - 21)
            A!![23] = A!![23] shl 56 or A!![23].ushr(64 - 56)
            A!![4] = A!![4] shl 27 or A!![4].ushr(64 - 27)
            A!![9] = A!![9] shl 20 or A!![9].ushr(64 - 20)
            A!![14] = A!![14] shl 39 or A!![14].ushr(64 - 39)
            A!![19] = A!![19] shl 8 or A!![19].ushr(64 - 8)
            A!![24] = A!![24] shl 14 or A!![24].ushr(64 - 14)
            bnn = A!![12].inv()
            kt = A!![6] or A!![12]
            c0 = A!![0] xor kt
            kt = bnn or A!![18]
            c1 = A!![6] xor kt
            kt = A!![18] and A!![24]
            c2 = A!![12] xor kt
            kt = A!![24] or A!![0]
            c3 = A!![18] xor kt
            kt = A!![0] and A!![6]
            c4 = A!![24] xor kt
            A!![0] = c0
            A!![6] = c1
            A!![12] = c2
            A!![18] = c3
            A!![24] = c4
            bnn = A!![22].inv()
            kt = A!![9] or A!![10]
            c0 = A!![3] xor kt
            kt = A!![10] and A!![16]
            c1 = A!![9] xor kt
            kt = A!![16] or bnn
            c2 = A!![10] xor kt
            kt = A!![22] or A!![3]
            c3 = A!![16] xor kt
            kt = A!![3] and A!![9]
            c4 = A!![22] xor kt
            A!![3] = c0
            A!![9] = c1
            A!![10] = c2
            A!![16] = c3
            A!![22] = c4
            bnn = A!![19].inv()
            kt = A!![7] or A!![13]
            c0 = A!![1] xor kt
            kt = A!![13] and A!![19]
            c1 = A!![7] xor kt
            kt = bnn and A!![20]
            c2 = A!![13] xor kt
            kt = A!![20] or A!![1]
            c3 = bnn xor kt
            kt = A!![1] and A!![7]
            c4 = A!![20] xor kt
            A!![1] = c0
            A!![7] = c1
            A!![13] = c2
            A!![19] = c3
            A!![20] = c4
            bnn = A!![17].inv()
            kt = A!![5] and A!![11]
            c0 = A!![4] xor kt
            kt = A!![11] or A!![17]
            c1 = A!![5] xor kt
            kt = bnn or A!![23]
            c2 = A!![11] xor kt
            kt = A!![23] and A!![4]
            c3 = bnn xor kt
            kt = A!![4] or A!![5]
            c4 = A!![23] xor kt
            A!![4] = c0
            A!![5] = c1
            A!![11] = c2
            A!![17] = c3
            A!![23] = c4
            bnn = A!![8].inv()
            kt = bnn and A!![14]
            c0 = A!![2] xor kt
            kt = A!![14] or A!![15]
            c1 = bnn xor kt
            kt = A!![15] and A!![21]
            c2 = A!![14] xor kt
            kt = A!![21] or A!![2]
            c3 = A!![15] xor kt
            kt = A!![2] and A!![8]
            c4 = A!![21] xor kt
            A!![2] = c0
            A!![8] = c1
            A!![14] = c2
            A!![15] = c3
            A!![21] = c4
            A!![0] = A!![0] xor RC[j + 0]

            tt0 = A!![6] xor A!![9]
            tt1 = A!![7] xor A!![5]
            tt0 = tt0 xor (A!![8] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![24] xor A!![22]
            tt3 = A!![20] xor A!![23]
            tt0 = tt0 xor A!![21]
            tt2 = tt2 xor tt3
            t0 = tt0 xor tt2

            tt0 = A!![12] xor A!![10]
            tt1 = A!![13] xor A!![11]
            tt0 = tt0 xor (A!![14] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![0] xor A!![3]
            tt3 = A!![1] xor A!![4]
            tt0 = tt0 xor A!![2]
            tt2 = tt2 xor tt3
            t1 = tt0 xor tt2

            tt0 = A!![18] xor A!![16]
            tt1 = A!![19] xor A!![17]
            tt0 = tt0 xor (A!![15] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![6] xor A!![9]
            tt3 = A!![7] xor A!![5]
            tt0 = tt0 xor A!![8]
            tt2 = tt2 xor tt3
            t2 = tt0 xor tt2

            tt0 = A!![24] xor A!![22]
            tt1 = A!![20] xor A!![23]
            tt0 = tt0 xor (A!![21] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![12] xor A!![10]
            tt3 = A!![13] xor A!![11]
            tt0 = tt0 xor A!![14]
            tt2 = tt2 xor tt3
            t3 = tt0 xor tt2

            tt0 = A!![0] xor A!![3]
            tt1 = A!![1] xor A!![4]
            tt0 = tt0 xor (A!![2] xor tt1)
            tt0 = tt0 shl 1 or tt0.ushr(63)
            tt2 = A!![18] xor A!![16]
            tt3 = A!![19] xor A!![17]
            tt0 = tt0 xor A!![15]
            tt2 = tt2 xor tt3
            t4 = tt0 xor tt2

            A!![0] = A!![0] xor t0
            A!![3] = A!![3] xor t0
            A!![1] = A!![1] xor t0
            A!![4] = A!![4] xor t0
            A!![2] = A!![2] xor t0
            A!![6] = A!![6] xor t1
            A!![9] = A!![9] xor t1
            A!![7] = A!![7] xor t1
            A!![5] = A!![5] xor t1
            A!![8] = A!![8] xor t1
            A!![12] = A!![12] xor t2
            A!![10] = A!![10] xor t2
            A!![13] = A!![13] xor t2
            A!![11] = A!![11] xor t2
            A!![14] = A!![14] xor t2
            A!![18] = A!![18] xor t3
            A!![16] = A!![16] xor t3
            A!![19] = A!![19] xor t3
            A!![17] = A!![17] xor t3
            A!![15] = A!![15] xor t3
            A!![24] = A!![24] xor t4
            A!![22] = A!![22] xor t4
            A!![20] = A!![20] xor t4
            A!![23] = A!![23] xor t4
            A!![21] = A!![21] xor t4
            A!![3] = A!![3] shl 36 or A!![3].ushr(64 - 36)
            A!![1] = A!![1] shl 3 or A!![1].ushr(64 - 3)
            A!![4] = A!![4] shl 41 or A!![4].ushr(64 - 41)
            A!![2] = A!![2] shl 18 or A!![2].ushr(64 - 18)
            A!![6] = A!![6] shl 1 or A!![6].ushr(64 - 1)
            A!![9] = A!![9] shl 44 or A!![9].ushr(64 - 44)
            A!![7] = A!![7] shl 10 or A!![7].ushr(64 - 10)
            A!![5] = A!![5] shl 45 or A!![5].ushr(64 - 45)
            A!![8] = A!![8] shl 2 or A!![8].ushr(64 - 2)
            A!![12] = A!![12] shl 62 or A!![12].ushr(64 - 62)
            A!![10] = A!![10] shl 6 or A!![10].ushr(64 - 6)
            A!![13] = A!![13] shl 43 or A!![13].ushr(64 - 43)
            A!![11] = A!![11] shl 15 or A!![11].ushr(64 - 15)
            A!![14] = A!![14] shl 61 or A!![14].ushr(64 - 61)
            A!![18] = A!![18] shl 28 or A!![18].ushr(64 - 28)
            A!![16] = A!![16] shl 55 or A!![16].ushr(64 - 55)
            A!![19] = A!![19] shl 25 or A!![19].ushr(64 - 25)
            A!![17] = A!![17] shl 21 or A!![17].ushr(64 - 21)
            A!![15] = A!![15] shl 56 or A!![15].ushr(64 - 56)
            A!![24] = A!![24] shl 27 or A!![24].ushr(64 - 27)
            A!![22] = A!![22] shl 20 or A!![22].ushr(64 - 20)
            A!![20] = A!![20] shl 39 or A!![20].ushr(64 - 39)
            A!![23] = A!![23] shl 8 or A!![23].ushr(64 - 8)
            A!![21] = A!![21] shl 14 or A!![21].ushr(64 - 14)
            bnn = A!![13].inv()
            kt = A!![9] or A!![13]
            c0 = A!![0] xor kt
            kt = bnn or A!![17]
            c1 = A!![9] xor kt
            kt = A!![17] and A!![21]
            c2 = A!![13] xor kt
            kt = A!![21] or A!![0]
            c3 = A!![17] xor kt
            kt = A!![0] and A!![9]
            c4 = A!![21] xor kt
            A!![0] = c0
            A!![9] = c1
            A!![13] = c2
            A!![17] = c3
            A!![21] = c4
            bnn = A!![14].inv()
            kt = A!![22] or A!![1]
            c0 = A!![18] xor kt
            kt = A!![1] and A!![5]
            c1 = A!![22] xor kt
            kt = A!![5] or bnn
            c2 = A!![1] xor kt
            kt = A!![14] or A!![18]
            c3 = A!![5] xor kt
            kt = A!![18] and A!![22]
            c4 = A!![14] xor kt
            A!![18] = c0
            A!![22] = c1
            A!![1] = c2
            A!![5] = c3
            A!![14] = c4
            bnn = A!![23].inv()
            kt = A!![10] or A!![19]
            c0 = A!![6] xor kt
            kt = A!![19] and A!![23]
            c1 = A!![10] xor kt
            kt = bnn and A!![2]
            c2 = A!![19] xor kt
            kt = A!![2] or A!![6]
            c3 = bnn xor kt
            kt = A!![6] and A!![10]
            c4 = A!![2] xor kt
            A!![6] = c0
            A!![10] = c1
            A!![19] = c2
            A!![23] = c3
            A!![2] = c4
            bnn = A!![11].inv()
            kt = A!![3] and A!![7]
            c0 = A!![24] xor kt
            kt = A!![7] or A!![11]
            c1 = A!![3] xor kt
            kt = bnn or A!![15]
            c2 = A!![7] xor kt
            kt = A!![15] and A!![24]
            c3 = bnn xor kt
            kt = A!![24] or A!![3]
            c4 = A!![15] xor kt
            A!![24] = c0
            A!![3] = c1
            A!![7] = c2
            A!![11] = c3
            A!![15] = c4
            bnn = A!![16].inv()
            kt = bnn and A!![20]
            c0 = A!![12] xor kt
            kt = A!![20] or A!![4]
            c1 = bnn xor kt
            kt = A!![4] and A!![8]
            c2 = A!![20] xor kt
            kt = A!![8] or A!![12]
            c3 = A!![4] xor kt
            kt = A!![12] and A!![16]
            c4 = A!![8] xor kt
            A!![12] = c0
            A!![16] = c1
            A!![20] = c2
            A!![4] = c3
            A!![8] = c4
            A!![0] = A!![0] xor RC[j + 1]
            t = A!![5]
            A!![5] = A!![18]
            A!![18] = A!![11]
            A!![11] = A!![10]
            A!![10] = A!![6]
            A!![6] = A!![22]
            A!![22] = A!![20]
            A!![20] = A!![12]
            A!![12] = A!![19]
            A!![19] = A!![15]
            A!![15] = A!![24]
            A!![24] = A!![8]
            A!![8] = t
            t = A!![1]
            A!![1] = A!![9]
            A!![9] = A!![14]
            A!![14] = A!![2]
            A!![2] = A!![13]
            A!![13] = A!![23]
            A!![23] = A!![4]
            A!![4] = A!![21]
            A!![21] = A!![16]
            A!![16] = A!![3]
            A!![3] = A!![17]
            A!![17] = A!![7]
            A!![7] = t
            j += 2
        }
    }

    /** @see DigestEngine
     */
    override fun doPadding(out: ByteArray?, off: Int) {
        val ptr = flush()
        val buf = blockBuffer
        if (ptr + 1 == buf.size) {
            buf[ptr] = 0x81.toByte()
        } else {
            buf[ptr] = 0x01.toByte()
            for (i in ptr + 1 until buf.size - 1)
                buf[i] = 0
            buf[buf.size - 1] = 0x80.toByte()
        }
        processBlock(buf)
        A!![1] = A!![1].inv()
        A!![2] = A!![2].inv()
        A!![8] = A!![8].inv()
        A!![12] = A!![12].inv()
        A!![17] = A!![17].inv()
        A!![20] = A!![20].inv()
        val dlen = digestLength
        var i = 0
        while (i < dlen) {
            encodeLELong(A!![i.ushr(3)], tmpOut!!, i)
            i += 8
        }
        System.arraycopy(tmpOut!!, 0, out, off, dlen)
    }

    /** @see DigestEngine
     */
    override fun doInit() {
        A = LongArray(25)
        tmpOut = ByteArray(digestLength + 7 and 7.inv())
        doReset()
    }

    private fun doReset() {
        for (i in 0..24)
            A!![i] = 0
        A!![1] = -0x1L
        A!![2] = -0x1L
        A!![8] = -0x1L
        A!![12] = -0x1L
        A!![17] = -0x1L
        A!![20] = -0x1L
    }

    /** @see DigestEngine
     */
    protected fun copyState(dst: KeccakCore): Digest {
        System.arraycopy(A!!, 0, dst.A!!, 0, 25)
        return super.copyState(dst)
    }

    /** @see Digest
     */
    override fun toString(): String {
        return "Keccak-" + (digestLength shl 3)
    }

    companion object {

        private val RC = longArrayOf(
            0x0000000000000001L,
            0x0000000000008082L,
            -0x7fffffffffff7f76L,
            -0x7fffffff7fff8000L,
            0x000000000000808BL,
            0x0000000080000001L,
            -0x7fffffff7fff7f7fL,
            -0x7fffffffffff7ff7L,
            0x000000000000008AL,
            0x0000000000000088L,
            0x0000000080008009L,
            0x000000008000000AL,
            0x000000008000808BL,
            -0x7fffffffffffff75L,
            -0x7fffffffffff7f77L,
            -0x7fffffffffff7ffdL,
            -0x7fffffffffff7ffeL,
            -0x7fffffffffffff80L,
            0x000000000000800AL,
            -0x7fffffff7ffffff6L,
            -0x7fffffff7fff7f7fL,
            -0x7fffffffffff7f80L,
            0x0000000080000001L,
            -0x7fffffff7fff7ff8L
        )

        /**
         * Encode the 64-bit word `val` into the array
         * `buf` at offset `off`, in little-endian
         * convention (least significant byte first).
         *
         * @param val   the value to encode
         * @param buf   the destination buffer
         * @param off   the destination offset
         */
        private fun encodeLELong(`val`: Long, buf: ByteArray, off: Int) {
            buf[off + 0] = `val`.toByte()
            buf[off + 1] = `val`.ushr(8).toByte()
            buf[off + 2] = `val`.ushr(16).toByte()
            buf[off + 3] = `val`.ushr(24).toByte()
            buf[off + 4] = `val`.ushr(32).toByte()
            buf[off + 5] = `val`.ushr(40).toByte()
            buf[off + 6] = `val`.ushr(48).toByte()
            buf[off + 7] = `val`.ushr(56).toByte()
        }

        /**
         * Decode a 64-bit little-endian word from the array `buf`
         * at offset `off`.
         *
         * @param buf   the source buffer
         * @param off   the source offset
         * @return  the decoded value
         */
        private fun decodeLELong(buf: ByteArray, off: Int): Long {
            return (buf[off + 0].toLong() and 0xFFL
                    or (buf[off + 1].toLong() and 0xFFL shl 8)
                    or (buf[off + 2].toLong() and 0xFFL shl 16)
                    or (buf[off + 3].toLong() and 0xFFL shl 24)
                    or (buf[off + 4].toLong() and 0xFFL shl 32)
                    or (buf[off + 5].toLong() and 0xFFL shl 40)
                    or (buf[off + 6].toLong() and 0xFFL shl 48)
                    or (buf[off + 7].toLong() and 0xFFL shl 56))
        }
    }
}
