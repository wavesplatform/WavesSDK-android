/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import android.app.Activity

interface Keeper {
    fun sign(activity: Activity, transaction: KeeperTransaction, callback: KeeperCallback)
    fun send(activity: Activity, transaction: KeeperTransaction, callback: KeeperCallback)
}