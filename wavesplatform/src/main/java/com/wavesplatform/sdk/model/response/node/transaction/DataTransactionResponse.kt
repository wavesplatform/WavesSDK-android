package com.wavesplatform.sdk.model.response.node.transaction

import android.os.Parcel
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.model.request.node.BaseTransaction
import com.wavesplatform.sdk.model.request.node.DataTransaction
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * See [com.wavesplatform.sdk.model.request.node.DataTransaction]
 */
@Parcelize
class DataTransactionResponse(@SerializedName("data")
                              var data: List<DataTransaction.Data>? = null)
    : BaseTransactionResponse(type = BaseTransaction.DATA), KeeperTransactionResponse {

    companion object : Parceler<DataTransactionResponse> {

        override fun DataTransactionResponse.write(parcel: Parcel, flags: Int) {
            parcel.apply {
                writeTypedList(data)
                writeBaseToParcel(this)
            }
        }

        override fun create(parcel: Parcel): DataTransactionResponse {
            return DataTransactionResponse(
                    mutableListOf<DataTransaction.Data>().apply {
                        parcel.readTypedList(this, DataTransaction.Data.CREATOR)
                    })
                    .apply {
                        readBaseFromParcel(parcel)
                    }
        }
    }
}