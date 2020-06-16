/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import android.content.Context
import android.content.Intent
import androidx.annotation.RestrictTo
import com.wavesplatform.sdk.keeper.model.KeeperDataHolder
import com.wavesplatform.sdk.keeper.model.KeeperIntentResult
import com.wavesplatform.sdk.keeper.model.KeeperProcessData

interface Keeper {
    fun configureDApp(
        context: Context,
        dAppName: String,
        dAppIconUrl: String
    )

    fun <T : KeeperTransaction> sign(
        activity: androidx.fragment.app.FragmentActivity,
        transaction: KeeperTransaction,
        callback: KeeperCallback<T>
    )

    fun <T : KeeperTransactionResponse> send(
        activity: androidx.fragment.app.FragmentActivity,
        transaction: KeeperTransaction,
        callback: KeeperCallback<T>
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun finishProcess(
        activity: androidx.fragment.app.FragmentActivity,
        result: KeeperIntentResult
    )

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun processData(intent: Intent): KeeperProcessData?

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isKeeperIntent(intent: Intent): Boolean

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun keeperDataHolder(): KeeperDataHolder?
}
