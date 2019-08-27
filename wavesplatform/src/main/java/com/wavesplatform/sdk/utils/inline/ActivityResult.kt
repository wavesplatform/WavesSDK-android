/*
 * Created by Eduard Zaydel on 27/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */
package com.wavesplatform.sdk.utils.inline

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.v4.app.FragmentManager

typealias OnActivityResult = (success: Boolean, data: Intent) -> Unit

data class ActivityResult(
        private var onActivityResult: OnActivityResult?,
        private var fragmentManager: FragmentManager?
) {
    private fun removeFragment(requestCode: Int) {
        val tag = InlineActivityResult.getTag(requestCode)
        val fragment = fragmentManager?.findFragmentByTag(tag) ?: return
        fragmentManager
                ?.beginTransaction()
                ?.apply { remove(fragment) }
                ?.commit()
        fragmentManager = null
    }

    fun deliverResult(requestCode: Int, resultCode: Int, data: Intent) {
        onActivityResult?.invoke(resultCode == RESULT_OK, data)
        onActivityResult = null
        removeFragment(requestCode)
    }
}
