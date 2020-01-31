package com.wavesplatform.sdk.model.response.data.transaction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.MassTransferTransaction]
 */
@Parcelize
class DataMassTransferTransactionResponse(@SerializedName("assetId")
                                          var assetId: String?,
                                          @SerializedName("attachment")
                                          var attachment: String,
                                          @SerializedName("transferCount")
                                          var transferCount: Int,
                                          @SerializedName("totalAmount")
                                          var totalAmount: Long,
                                          @SerializedName("transfers")
                                          var transfers: MutableList<Transfer>)
    : BaseDataTransactionResponse(type = BaseTransaction.MASS_TRANSFER), Parcelable {
    /**
     * The item of the Mass-transfer transaction
     */
    @Parcelize
    class Transfer(
            /**
             * Address or alias of Waves blockchain
             */
            @SerializedName("recipient") var recipient: String = "",
            @SerializedName("amount") var amount: Double = 0.0
    ) : Parcelable
}