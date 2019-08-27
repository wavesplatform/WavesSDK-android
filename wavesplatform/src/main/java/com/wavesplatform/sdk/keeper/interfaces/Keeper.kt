/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import android.support.v4.app.FragmentActivity

interface Keeper {
    fun <T: KeeperTransaction> sign(activity: FragmentActivity,
                                    transaction: KeeperTransaction,
                                    callback: KeeperCallback<T>)
    fun <T: KeeperTransactionResponse> send(activity: FragmentActivity,
                                    transaction: KeeperTransaction,
                                    callback: KeeperCallback<T>)
}