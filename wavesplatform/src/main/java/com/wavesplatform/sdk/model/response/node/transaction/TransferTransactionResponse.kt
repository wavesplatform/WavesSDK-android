package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.TransferTransaction]
 */
@Parcelize
class TransferTransactionResponse(
    @SerializedName("assetId")
    var assetId: String? = "",
    @SerializedName("recipient")
    var recipient: String = "",
    @SerializedName("amount")
    var amount: Long = 0L,
    @SerializedName("attachment")
    var attachment: String? = "",
    @SerializedName("feeAssetId")
    var feeAssetId: String? = ""
) :
    BaseTransactionResponse(type = BaseTransaction.TRANSFER), KeeperTransactionResponse {

    companion object : Parceler<TransferTransactionResponse> {

        override fun TransferTransactionResponse.write(parcel: Parcel, flags: Int) {
            parcel.apply {
                writeString(assetId)
                writeString(recipient)
                writeLong(amount)
                writeString(attachment)
                writeString(feeAssetId)
                writeBaseToParcel(this)
            }
        }

        override fun create(parcel: Parcel): TransferTransactionResponse {
            return TransferTransactionResponse(
                parcel.readString().orEmpty(),
                parcel.readString().orEmpty(),
                parcel.readLong(),
                parcel.readString().orEmpty(),
                parcel.readString().orEmpty()
            )
                .apply {
                    readBaseFromParcel(parcel)
                }
        }
    }
}
