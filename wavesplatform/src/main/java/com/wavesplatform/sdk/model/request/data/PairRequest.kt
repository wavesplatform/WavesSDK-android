package com.wavesplatform.sdk.model.request.data

import com.google.gson.annotations.SerializedName

/**
 * DEX volume, change24, last trade price
 * 1) Get list of pairs info by serialized pairs list
 * 2) Get all list of pairs info by limit (sort by volume in WAVES)
 */
data class PairRequest(

    /**
     * Get pair info by amount and price assets
     */
    @SerializedName("pairs") var pairs: List<String>? = null,

    /**
     * For searching pairs, that have the {searchByAsset} in asset names,
     * tickers, id of one asset of the pair.
     */
    @SerializedName("search_by_asset") var searchByAsset: String? = null,

    /**
     * For searching pairs, that have the {searchByAssets} in asset names,
     * tickers, id of one asset of the pair.
     */
    @SerializedName("search_by_assets") var searchByAssets: List<String>? = null,

    /**
     * Whether to search assets of pairs exactly or not.
     * Parameter position is corresponds to asset position.
     */
    @SerializedName("match_exactly") var matchExactly: Boolean? = null,

    /**
     * How many pairs to await in response.
     */
    @SerializedName("limit") var limit: Int = 100,

    /**
     * Matcher address
     */
    @SerializedName("matcher") var matcher: String? = null
)
