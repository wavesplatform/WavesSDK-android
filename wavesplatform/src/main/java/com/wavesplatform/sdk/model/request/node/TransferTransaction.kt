/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.request.node

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.common.primitives.Bytes
import com.google.common.primitives.Longs
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.parseAlias

/**
 * Transfer transaction sends amount of asset on address.
 * It is used to transfer a specific amount of an asset (WAVES by default)
 * to the recipient (by address or alias).
 */
open class TransferTransaction(
        /**
         * Id of transferable asset in Waves blockchain, different for main and test net
         */
        @SerializedName("assetId") var assetId: String = "",
        /**
         * Address or alias of Waves blockchain
         */
        @SerializedName("recipient") var recipient: String = "",
        /**
         * Amount of asset in satoshi
         */
        @SerializedName("amount") var amount: Long = 0,
        /**
         * Additional info [0,140] bytes of string encoded in Base58
         */
        @SerializedName("attachment") var attachment: String = "",
        /**
         * Asset id instead Waves for transaction commission withdrawal
         */
        @SerializedName("feeAssetId") var feeAssetId: String = "")
    : BaseTransaction(TRANSFER), Parcelable, KeeperTransaction {


    override fun toBytes(): ByteArray {
        return try {
            Bytes.concat(
                    byteArrayOf(type),
                    byteArrayOf(version),
                    WavesCrypto.base58decode(senderPublicKey),
                    SignUtil.arrayOption(assetId),
                    SignUtil.arrayOption(feeAssetId),
                    Longs.toByteArray(timestamp),
                    Longs.toByteArray(amount),
                    Longs.toByteArray(fee),
                    SignUtil.recipientBytes(recipient.parseAlias(), version, chainId),
                    SignUtil.attachmentBytes(attachment)
            )
        } catch (e: Exception) {
            Log.e("Sign", "Can't create bytes for sign in Transfer Transaction", e)
            ByteArray(0)
        }
    }

    override fun sign(seed: String): String {
        signature = super.sign(seed)
        return signature ?: ""
    }

    private constructor(parcel: Parcel) : this() {
        assetId = parcel.readString() ?: ""
        recipient = parcel.readString() ?: ""
        amount = parcel.readLong()
        attachment = parcel.readString() ?: ""
        feeAssetId = parcel.readString() ?: ""

        senderPublicKey = parcel.readString() ?: ""
        timestamp = parcel.readLong()
        fee = parcel.readLong()
        version = parcel.readByte()
        parcel.readStringList(proofs)
        signature = parcel.readString() ?: ""
        chainId = parcel.readByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assetId)
        parcel.writeString(recipient)
        parcel.writeLong(amount)
        parcel.writeString(attachment)
        parcel.writeString(feeAssetId)

        parcel.writeString(senderPublicKey)
        parcel.writeLong(timestamp)
        parcel.writeLong(fee)
        parcel.writeByte(version)
        parcel.writeStringList(proofs)
        parcel.writeString(signature)
        parcel.writeByte(chainId)
    }

    override fun describeContents() = 0

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<TransferTransaction> {
            override fun createFromParcel(parcel: Parcel) = TransferTransaction(parcel)
            override fun newArray(size: Int) = arrayOfNulls<TransferTransaction>(size)
        }

        fun getAttachmentSize(attachment: String?): Int {
            if (attachment == null) {
                return 0
            }
            return try {
                attachment.toByteArray().size
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }

        const val MAX_ATTACHMENT_SIZE = 140
    }
}