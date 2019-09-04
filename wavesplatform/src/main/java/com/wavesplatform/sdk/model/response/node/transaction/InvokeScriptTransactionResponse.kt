package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction

/**
 * See [com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction]
 */
class InvokeScriptTransactionResponse(@SerializedName("feeAssetId")
                                      var feeAssetId: String?,
                                      @SerializedName("dApp")
                                      var dApp: String,
                                      @SerializedName("call")
                                      var call: InvokeScriptTransaction.Call?,
                                      @SerializedName("payment")
                                      var payment: Array<InvokeScriptTransaction.Payment>)
    : BaseTransactionResponse(type = BaseTransaction.SCRIPT_INVOCATION), Parcelable, KeeperTransactionResponse {

    constructor(parcel: Parcel) : this(
            feeAssetId = parcel.readString() ?: "",
            dApp = parcel.readString() ?: "",
            call = parcel.readParcelable(InvokeScriptTransaction.Call::class.java.classLoader),
            payment = getPayment(parcel)) {

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
        parcel.writeString(feeAssetId)
        parcel.writeString(dApp)
        parcel.writeParcelable(call, flags)
        parcel.writeTypedArray(payment, flags)

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

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InvokeScriptTransactionResponse> {

        fun getPayment(parcel: Parcel): Array<InvokeScriptTransaction.Payment> {
            val tempPayment = arrayOf<InvokeScriptTransaction.Payment>()
            parcel.readTypedArray(tempPayment, InvokeScriptTransaction.Payment.CREATOR)
            return tempPayment
        }

        override fun createFromParcel(parcel: Parcel): InvokeScriptTransactionResponse {
            return InvokeScriptTransactionResponse(parcel)
        }

        override fun newArray(size: Int): Array<InvokeScriptTransactionResponse?> {
            return arrayOfNulls(size)
        }
    }
}