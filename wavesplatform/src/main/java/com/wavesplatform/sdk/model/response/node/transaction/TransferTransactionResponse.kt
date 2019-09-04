package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.TransferTransaction]
 */
class TransferTransactionResponse(@SerializedName("assetId")
                                  var assetId: String? = "",
                                  @SerializedName("recipient")
                                  var recipient: String = "",
                                  @SerializedName("amount")
                                  var amount: Long = 0L,
                                  @SerializedName("attachment")
                                  var attachment: String? = "",
                                  @SerializedName("feeAssetId")
                                  var feeAssetId: String? = "")
    : BaseTransactionResponse(type = BaseTransaction.TRANSFER), Parcelable, KeeperTransactionResponse {

    private constructor(parcel: Parcel) : this() {
        assetId = parcel.readString() ?: ""
        recipient = parcel.readString() ?: ""
        amount = parcel.readLong()
        attachment = parcel.readString() ?: ""
        feeAssetId = parcel.readString() ?: ""

        id = parcel.readString()
        sender = parcel.readString() ?: ""
        senderPublicKey = parcel.readString() ?: ""
        timestamp = parcel.readLong()
        fee = parcel.readLong()
        // chainId = parcel.readByte()
        version = parcel.readByte()
        parcel.readStringList(proofs)
        signature = parcel.readString() ?: ""
        height = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assetId)
        parcel.writeString(recipient)
        parcel.writeLong(amount)
        parcel.writeString(attachment)
        parcel.writeString(feeAssetId)

        parcel.writeString(id)
        parcel.writeString(sender)
        parcel.writeString(senderPublicKey)
        parcel.writeLong(timestamp)
        parcel.writeLong(fee)
        // parcel.writeByte(chainId ?: WavesSdk.getEnvironment().chainId)
        parcel.writeByte(version)
        parcel.writeStringList(proofs)
        parcel.writeString(signature)
        parcel.writeLong(height ?: 0)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<TransferTransactionResponse> {
            override fun createFromParcel(parcel: Parcel) = TransferTransactionResponse(parcel)
            override fun newArray(size: Int) = arrayOfNulls<TransferTransactionResponse>(size)
        }
    }
}