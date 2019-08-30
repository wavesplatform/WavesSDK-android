/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: Throwable) {
        false
    }
}

fun Context.isIntentAvailable(action: String)
        = packageManager?.queryIntentActivities(Intent(action), PackageManager.MATCH_DEFAULT_ONLY)?.any() ?: false