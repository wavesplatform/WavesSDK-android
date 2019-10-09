/*
 * Created by Eduard Zaydel on 9/10/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.request.data

import com.google.gson.annotations.SerializedName

/**
 * Assets information
 * 1) Get list of assets info by ids list param
 * 2) Get list of assets info by search param
 */
data class AssetsRequest(

        /**
         * Get assets info by assets ids
         */
        @SerializedName("ids") var ids: List<String?>? = null,

        /**
         * For searching assets, that have the {search} in asset names,
         * tickers, id of one asset of the pair.
         */
        @SerializedName("search") var search: String? = null,

        /**
         * How many assets to await in response.
         */
        @SerializedName("limit") var limit: Int? = null)