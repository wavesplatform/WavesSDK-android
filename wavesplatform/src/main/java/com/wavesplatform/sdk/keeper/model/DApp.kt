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
                .putString(KeeperKeys.DApp.NAME, name)
                .putString(KeeperKeys.DApp.ICON_URL, iconUrl)
                .apply()
    }

    companion object {
        fun restore(preferences: SharedPreferences): DApp {
            return DApp(preferences.getString(KeeperKeys.DApp.NAME, ""),
                    preferences.getString(KeeperKeys.DApp.ICON_URL, ""))
        }
    }
}