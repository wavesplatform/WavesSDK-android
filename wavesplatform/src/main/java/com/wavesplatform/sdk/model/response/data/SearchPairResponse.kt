package com.wavesplatform.sdk.model.response.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * If Search it returns the list op DEX pairs with amountAsset and priceAsset
 * If it Request with special pairs order, it's return order list same length
 * with data if pair exist and null if not
 */
@Parcelize
data class SearchPairResponse(
        @SerializedName("__type") var type: String = "list",
        @SerializedName("data") var data: List<Pair> = mutableListOf()) : Parcelable {

    @Parcelize
    data class Pair(
            @SerializedName("__type") var type: String = "pair",
            @SerializedName("data") var data: Data? = null,
            @SerializedName("amountAsset") var amountAsset: String?,
            @SerializedName("priceAsset") var priceAsset: String?) : Parcelable {

        @Parcelize
        data class Data(
                @SerializedName("firstPrice") var firstPrice: Double = 0.0,
                @SerializedName("lastPrice") var lastPrice: Double = 0.0,
                @SerializedName("low") var low: Double = 0.0,
                @SerializedName("high") var high: Double = 0.0,
                @SerializedName("weightedAveragePrice") var weightedAveragePrice: Double = 0.0,
                @SerializedName("volume") var volume: Double = 0.0,
                @SerializedName("quoteVolume") var quoteVolume: Double? = 0.0,
                @SerializedName("volumeWaves") var volumeWaves: Double? = 0.0,
                @SerializedName("txsCount") var txsCount: Long? = null) : Parcelable
    }
}