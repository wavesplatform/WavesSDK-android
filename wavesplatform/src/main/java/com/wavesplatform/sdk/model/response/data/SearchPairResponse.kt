package com.wavesplatform.sdk.model.response.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class SearchPairResponse(
        @SerializedName("__type") var type: String = "list",
        @SerializedName("data") var data: List<Pair> = mutableListOf()) : Parcelable {

    @Parcelize
    data class Pair(
            @SerializedName("__type") var type: String = "pair",
            @SerializedName("data") var data: Data? = null,
            @SerializedName("amountAsset") var amountAsset: String,
            @SerializedName("priceAsset") var priceAsset: String) : Parcelable {

        @Parcelize
        data class Data(
                @SerializedName("firstPrice") var firstPrice: BigDecimal = BigDecimal(0),
                @SerializedName("lastPrice") var lastPrice: BigDecimal = BigDecimal(0),
                @SerializedName("low") var low: BigDecimal = BigDecimal(0),
                @SerializedName("high") var high: BigDecimal = BigDecimal(0),
                @SerializedName("weightedAveragePrice") var weightedAveragePrice: BigDecimal = BigDecimal(0),
                @SerializedName("volume") var volume: BigDecimal = BigDecimal(0),
                @SerializedName("quoteVolume") var quoteVolume: BigDecimal? = BigDecimal(0),
                @SerializedName("volumeWaves") var volumeWaves: BigDecimal? = BigDecimal(0),
                @SerializedName("txsCount") var txsCount: Long? = null) : Parcelable
    }
}