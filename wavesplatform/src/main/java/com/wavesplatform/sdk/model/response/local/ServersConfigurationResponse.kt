package com.wavesplatform.sdk.model.response.local

import com.google.gson.annotations.SerializedName

data class ServersConfigurationResponse(
    @SerializedName("name") var name: String = "",
    @SerializedName("servers") var servers: Servers = Servers(),
    @SerializedName("scheme") var scheme: String = ""
) {

    data class Servers(
        @SerializedName("nodeUrl") var nodeUrl: String = "",
        @SerializedName("dataUrl") var dataUrl: String = "",
        @SerializedName("matcherUrl") var matcherUrl: String = ""
    )
}
