package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction]
 */
@Parcelize
class InvokeScriptTransactionResponse(
    @SerializedName("feeAssetId")
    var feeAssetId: String?,
    @SerializedName("dApp")
    var dApp: String,
    @SerializedName("call")
    var call: InvokeScriptTransaction.Call?,
    @SerializedName("payment") var payment: List<InvokeScriptTransaction.Payment> = mutableListOf()
) : BaseTransactionResponse(type = BaseTransaction.SCRIPT_INVOCATION), KeeperTransactionResponse {

    companion object : Parceler<InvokeScriptTransactionResponse> {

        override fun InvokeScriptTransactionResponse.write(parcel: Parcel, flags: Int) {
            parcel.apply {
                writeString(feeAssetId)
                writeString(dApp)
                writeParcelable(call, flags)
                writeTypedList(payment)
                writeBaseToParcel(this)
            }
        }

        override fun create(parcel: Parcel): InvokeScriptTransactionResponse {
            return InvokeScriptTransactionResponse(
                parcel.readString().orEmpty(),
                parcel.readString().orEmpty(),
                parcel.readParcelable(InvokeScriptTransaction.Call::class.java.classLoader),
                parcel.createTypedArrayList(InvokeScriptTransaction.Payment.CREATOR).orEmpty()
            )
                .apply {
                    readBaseFromParcel(parcel)
                }
        }
    }
}
