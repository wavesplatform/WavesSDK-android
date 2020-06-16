package com.wavesplatform.sdk.model.response.data.transaction

import com.google.gson.annotations.SerializedName

open class BaseDataListTransactionsResponse<T> constructor(
    @SerializedName("__type")
    var type: String = "",
    @SerializedName("data")
    var data: List<DataResponse<T>> = listOf(),
    @SerializedName("lastCursor")
    var lastCursor: String = ""
) {

    data class DataResponse<T>(
        @SerializedName("__type")
        var type: String = "",
        @SerializedName("data")
        var transaction: T
    )
}
