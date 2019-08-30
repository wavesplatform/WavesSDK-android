/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.model.response.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class CandlesResponse(
        @SerializedName("__type")
        var type: String = "list",
        @SerializedName("data")
        var data: List<Data> = mutableListOf()) : Parcelable {

    @Parcelize
    data class Data(
            @SerializedName("__type")
            var type: String = "candle",
            @SerializedName("data")
            var data: CandleResponse) : Parcelable {

        @Parcelize
        data class CandleResponse(
                @SerializedName("close")
                var close: Double? = 0.0,
                @SerializedName("high")
                var high: Double? = 0.0,
                @SerializedName("low")
                var low: Double? = 0.0,
                @SerializedName("open")
                var openValue: Double? = 0.0,
                @SerializedName("time")
                var time: String = "",
                @SerializedName("txsCount")
                var txsCount: Int? = 0,
                @SerializedName("volume")
                var volume: Double? = 0.0) : Parcelable {

            private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

            fun getTimeInMillis(): Long {
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = dateFormat.parse(time)
                dateFormat.timeZone = TimeZone.getDefault()
                return dateFormat.parse(dateFormat.format(date)).time
            }
        }
    }
}