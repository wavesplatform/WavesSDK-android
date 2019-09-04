package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.DataTransaction

/**
 * See [com.wavesplatform.sdk.model.request.node.DataTransaction]
 */
class DataTransactionResponse(@SerializedName("data")
                              var data: List<DataTransaction.Data>? = null)
    : BaseTransactionResponse(type = BaseTransaction.DATA), Parcelable, KeeperTransactionResponse {


    private constructor(parcel: Parcel) : this() {
        val tempData = mutableListOf<DataTransaction.Data>()
        parcel.readTypedList(tempData, DataTransaction.Data.CREATOR)
        if (tempData.isNotEmpty()) {
            data = tempData
        }
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
        parcel.writeTypedList(data)
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
        val CREATOR = object : Parcelable.Creator<DataTransactionResponse> {
            override fun createFromParcel(parcel: Parcel) = DataTransactionResponse(parcel)
            override fun newArray(size: Int) = arrayOfNulls<DataTransactionResponse>(size)
        }
    }
}