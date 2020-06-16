/*
 * Created by Eduard Zaydel on 9/10/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.request.data

import com.google.gson.annotations.SerializedName

/**
 * Assets information
 * Get long list of assets info by ids list param
 */
data class AssetsRequest(

    /**
     * Get assets info by assets ids
     */
    @SerializedName("ids") var ids: List<String?>? = mutableListOf()
)
