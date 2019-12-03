package com.wavesplatform.sdk.model.request.data

import com.google.gson.annotations.SerializedName

/**
 * Request body to get pairs rates
 */
data class PairRatesRequest(

        /**
         * Get pair info by amount and price assets
         */
        @SerializedName("pairs") var pairs: List<String>? = null)