/*
 * Created by Eduard Zaydel on 27/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.utils.inline

import android.content.Intent
import android.support.v4.app.FragmentManager

class InlineActivityResult {
    private var pending: MutableMap<Int, Result> = mutableMapOf()

    fun start(
            fragmentManager: FragmentManager,
            intent: Intent,
            requestCode: Int,
            onActivityResult: OnActivityResult
    ) {
        check(!pending.containsKey(requestCode)) {
            "There is already a pending request for requestCode $requestCode."
        }

        pending[requestCode] = Result(onActivityResult = onActivityResult, fragmentManager = fragmentManager)
        val fragment = ActivityResultFragment.newInstance(launchIntent = intent, requestCode = requestCode)

        fragmentManager
                .beginTransaction()
                .apply { add(fragment, getTag(requestCode)) }
                .commit()
    }

    fun stopWithResult(requestCode: Int, resultCode: Int, data: Intent) {
        val pendingRequest = pending[requestCode]
                ?: throw IllegalStateException("There's no pending request for requestCode $requestCode.")

        pendingRequest.deliverResult(requestCode = requestCode, resultCode = resultCode, data = data)
        pending.remove(requestCode)
    }

    companion object {
        private var instanceCreator: (() -> InlineActivityResult)? = null

        fun getInstance(): InlineActivityResult {
            val defaultCreator = {
                instance ?: InlineActivityResult().also { instance = it }
            }
            return instanceCreator?.invoke() ?: defaultCreator()
        }

        fun getTag(requestCode: Int): String = "${TAG_FRAGMENT_PREFIX}_$requestCode"

        private const val TAG_FRAGMENT_PREFIX = "tag_inline_activity_result_"
        private var instance: InlineActivityResult? = null
    }
}
