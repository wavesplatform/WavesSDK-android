/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.model

import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction

sealed class KeeperResult {
    class Success<T : KeeperTransaction>(val transaction: T?) : KeeperResult()
    class Error(val message: String?, val errorCode: Int) : KeeperResult()

    companion object {
        const val CANCELED = 1
        const val UNKNOWN_ERROR = 2
    }
}