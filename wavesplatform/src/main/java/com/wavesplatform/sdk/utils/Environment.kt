package com.wavesplatform.sdk.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.model.response.local.ServersConfigurationResponse

/**
 * Settings for work of SDK with nodes and other Waves net-services.
 * It contains urls and time correction
 */
class Environment(
    @SerializedName("server") var server: Server,
    @SerializedName("timestampServerDiff") var timestampServerDiff: Long
) {

    @SerializedName("nodeUrl")
    var nodeUrl: String = ""
    @SerializedName("dataUrl")
    var dataUrl: String = ""
    @SerializedName("matcherUrl")
    var matcherUrl: String = ""
    @SerializedName("scheme")
    var chainId: Byte = WavesCrypto.MAIN_NET_CHAIN_ID

    fun init(context: Context) {
        val gson = Gson()
        val configurationResponse =
            when (server) {
                Server.MainNet -> {
                    gson.fromJson(
                        context.loadJsonFromAsset(WavesConstants.Environments.FILENAME_MAIN_NET),
                        ServersConfigurationResponse::class.java
                    )
                }
                Server.TestNet -> {
                    gson.fromJson(
                        context.loadJsonFromAsset(WavesConstants.Environments.FILENAME_TEST_NET),
                        ServersConfigurationResponse::class.java
                    )
                }
                Server.StageNet -> {
                    gson.fromJson(
                        context.loadJsonFromAsset(WavesConstants.Environments.FILENAME_STAGE_NET),
                        ServersConfigurationResponse::class.java
                    )
                }
                is Server.Custom -> {
                    val serverCustom = server as Server.Custom
                    ServersConfigurationResponse(
                        "Custom", ServersConfigurationResponse.Servers(
                            nodeUrl = serverCustom.node,
                            dataUrl = serverCustom.data,
                            matcherUrl = serverCustom.matcher
                        ), String(byteArrayOf(serverCustom.scheme)))
                }
            }
        this.chainId = configurationResponse.scheme.first().toByte()
        this.dataUrl = configurationResponse.servers.dataUrl
        this.nodeUrl = configurationResponse.servers.nodeUrl
        this.matcherUrl = configurationResponse.servers.matcherUrl
    }

    fun getTime(): Long {
        return System.currentTimeMillis() + timestampServerDiff
    }

    companion object {
        val DEFAULT = Environment(server = Server.MainNet, timestampServerDiff = 0L)
        val MAIN_NET = DEFAULT
        val TEST_NET = Environment(server = Server.TestNet, timestampServerDiff = 0L)
        val STAGE_NET = Environment(server = Server.StageNet, timestampServerDiff = 0L)
    }

    sealed class Server {
        object MainNet : Server()
        object TestNet : Server()
        object StageNet : Server()
        class Custom(val node: String, val matcher: String, val data: String, val scheme: Byte) :
            Server()
    }
}