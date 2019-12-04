package com.wavesplatform.sdk.model.request.data

import com.google.gson.annotations.SerializedName

/**
 * Request body to get pairs rates
 */
data class PairRatesRequest(

        /**
         * Get pairs rates info by amount and price assets
         */
        @SerializedName("pairs") var pairs: List<String>? = null,
        /**
         * Timestamp of rate info
         */
        @SerializedName("timestamp") var timestamp: Long? = null)