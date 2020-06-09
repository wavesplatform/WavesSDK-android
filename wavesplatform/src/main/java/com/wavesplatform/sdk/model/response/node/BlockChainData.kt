/*
 * Created by Eduard Zaydel on 4/2/2020
 * Copyright © 2020 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.response.node

import com.google.gson.annotations.SerializedName

data class BlockChainData(
    @SerializedName("key")
    var key: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("value")
    var value: Int
)
