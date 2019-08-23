/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.model

class KeeperResult @JvmOverloads constructor(val data: String?, val errorCode: Int = 0) {

    val isSuccess: Boolean
        get() = !isError

    val isError: Boolean
        get() = errorCode != 0
}