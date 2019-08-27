/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.model

import android.content.SharedPreferences
import com.wavesplatform.sdk.keeper.KeeperKeys

data class DApp(var name: String?, var iconUrl: String?) {

    fun save(preferences: SharedPreferences) {
        preferences
                .edit()
                .putString(KeeperKeys.DAppKeys.NAME, name)
                .putString(KeeperKeys.DAppKeys.ICON_URL, iconUrl)
                .apply()
    }

    companion object {
        fun restore(preferences: SharedPreferences): DApp {
            val dApp = DApp(preferences.getString(KeeperKeys.DAppKeys.NAME, ""),
                    preferences.getString(KeeperKeys.DAppKeys.ICON_URL, ""))

            check(!(dApp.name.isNullOrBlank() || dApp.iconUrl.isNullOrEmpty())) {
                "You must configure dApp with WavesSdk.keeper().configureDApp() before use Keeper"
            }

            return dApp
        }
    }
}