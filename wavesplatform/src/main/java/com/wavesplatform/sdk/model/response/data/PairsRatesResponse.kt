package com.wavesplatform.sdk.model.response.data

import com.google.gson.annotations.SerializedName

data class PairsRatesResponse(
    @SerializedName("__type") val type: String,
    @SerializedName("data") val data: List<Data>
)

data class Data(
    @SerializedName("__type") val type: String,
    @SerializedName("data") val rate: Rate,
    @SerializedName("amountAsset") val amountAsset: String,
    @SerializedName("priceAsset") val priceAsset: String
)

data class Rate(@SerializedName("rate") val price: Double)