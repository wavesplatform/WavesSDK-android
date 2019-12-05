package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.MassTransferTransaction.Transfer
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.MassTransferTransaction]
 */
@Parcelize
class MassTransferTransactionResponse(@SerializedName("assetId")
                                      var assetId: String?,
                                      @SerializedName("attachment")
                                      var attachment: String,
                                      @SerializedName("transferCount")
                                      var transferCount: Int,
                                      @SerializedName("totalAmount")
                                      var totalAmount: Long,
                                      @SerializedName("transfers")
                                      var transfers: Array<Transfer>)
    : BaseTransactionResponse(type = BaseTransaction.MASS_TRANSFER), Parcelable