/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import android.content.Context
import android.content.Intent
import android.support.annotation.RestrictTo
import android.support.v4.app.FragmentActivity
import com.wavesplatform.sdk.keeper.model.KeeperProcessData

interface Keeper {
    fun configureDApp(context: Context,
                      dAppName: String,
                      dAppIconUrl: String)

    fun <T : KeeperTransaction> sign(activity: FragmentActivity,
                                     transaction: KeeperTransaction,
                                     callback: KeeperCallback<T>)

    fun <T : KeeperTransactionResponse> send(activity: FragmentActivity,
                                             transaction: KeeperTransaction,
                                             callback: KeeperCallback<T>)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun finishSign(activity: FragmentActivity,
                   transaction: KeeperTransaction)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun finishSend(activity: FragmentActivity,
                   transaction: KeeperTransactionResponse)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun finishSign(activity: FragmentActivity,
                   error: String)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun finishSend(activity: FragmentActivity,
                   error: String)

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun processData(intent: Intent): KeeperProcessData?

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isKeeperIntent(intent: Intent): Boolean
}