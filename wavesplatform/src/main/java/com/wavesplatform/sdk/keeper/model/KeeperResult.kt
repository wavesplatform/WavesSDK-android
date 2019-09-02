/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.model

sealed class KeeperResult {
    data class Success<T>(val transaction: T?) : KeeperResult()
    data class Error(val message: String?, val code: Int) : KeeperResult()

    companion object {
        const val REJECTED = 1
        const val UNKNOWN_ERROR = 2
    }
}