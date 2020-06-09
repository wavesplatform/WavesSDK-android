package com.wavesplatform.sdk.model.response.data

import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.WavesConstants

data class DataServiceMassTransferTransactionResponse(
    @SerializedName("__type") val type: String,
    @SerializedName("isLastPage") val isLastPage: Boolean?,
    @SerializedName("lastCursor") val lastCursor: String?,
    @SerializedName("data") val data: List<Data>
) {
    data class Data(
        @SerializedName("__type") val type: String,
        @SerializedName("data") val transaction: Transaction
    )

    data class Transaction(
        @SerializedName("assetId") val assetId: String?,
        @SerializedName("attachment") val attachment: String,
        @SerializedName("transfers") val transfers: List<Transfer>,
        @SerializedName("type") val type: Byte,
        @SerializedName("id") val id: String? = null,
        @SerializedName("sender") val sender: String = "",
        @SerializedName("senderPublicKey") val senderPublicKey: String = "",
        @SerializedName("timestamp") val timestamp: String,
        @SerializedName("fee") val fee: Number = WavesConstants.WAVES_MIN_FEE,
        @SerializedName("chainId") val chainId: Byte? = WavesSdk.getEnvironment().chainId,
        @SerializedName("version") val version: Byte = 2,
        @SerializedName("proofs") val proofs: MutableList<String> = mutableListOf(),
        @SerializedName("signature") val signature: String = "",
        @SerializedName("height") val height: Long? = null
    )

    data class Transfer(
        @SerializedName("recipient") val recipient: String = "",
        @SerializedName("amount") val amount: Double = 0.0
    )
}
