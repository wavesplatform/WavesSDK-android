package com.wavesplatform.sdk.utils

import com.wavesplatform.sdk.model.response.data.AssetInfoResponse
import java.math.BigDecimal
import java.math.BigInteger

object WavesConstants {

    object Environments {
        const val FILENAME_MAIN_NET = "environment_mainnet.json"
        const val FILENAME_TEST_NET = "environment_testnet.json"
        const val FILENAME_STAGE_NET = "environment_stagenet.json"
    }

    const val VERSION: Byte = 2
    const val WAVES_ASSET_ID_EMPTY = ""
    const val WAVES_ASSET_ID_FILLED = "WAVES"
    const val SELL_ORDER_TYPE = "sell"
    const val BUY_ORDER_TYPE = "buy"
    const val CUSTOM_FEE_ASSET_NAME = "Waves"
    const val WAVES_MIN_FEE = 100000L
    const val WAVES_ORDER_MIN_FEE = 300000L
    const val WAVES_INVOKE_MIN_FEE = 500000L
    const val MIN_WAVES_SPONSORED_BALANCE = 1.005

    val WAVES_ASSET_INFO = AssetInfoResponse(
        id = WAVES_ASSET_ID_EMPTY,
        precision = 8,
        name = "WAVES",
        quantity = BigDecimal.valueOf(10000000000000000L)
    )
}
