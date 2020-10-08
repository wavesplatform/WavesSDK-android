/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.response.node

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class AssetsDetailsResponse(
    @SerializedName("assetId") var assetId: String = "",
    @SerializedName("issueHeight") var issueHeight: Long = 0L,
    @SerializedName("issueTimestamp") var issueTimestamp: Long = 0L,
    @SerializedName("issuer") var issuer: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("decimals") var decimals: Int = 8,
    @SerializedName("reissuable") var reissuable: Boolean? = null,
    @SerializedName("quantity") var quantity: BigDecimal = BigDecimal.ZERO,
    @SerializedName("scripted") var scripted: Boolean = false,
    @SerializedName("minSponsoredAssetFee") var minSponsoredAssetFee: Long = 0L
) : Parcelable
