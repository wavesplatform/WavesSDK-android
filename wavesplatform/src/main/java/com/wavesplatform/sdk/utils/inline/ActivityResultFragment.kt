/*
 * Created by Eduard Zaydel on 27/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */
package com.wavesplatform.sdk.utils.inline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

class ActivityResultFragment : Fragment() {

    private val ERR_MSG_INTENT = "Intent args must be provided"
    private val ERR_MSG_REQUEST_CODE = "Non-zero request code args must be provided"
    private val ERR_MSG_STARTED = "Started args must be provided"

    private var started: Boolean = false
        get() {
            return arguments?.getBoolean(KEY_STARTED)
                    ?: throw IllegalStateException(ERR_MSG_STARTED)
        }
        set(value) {
            field = value
            arguments?.putBoolean(KEY_STARTED, value) ?: throw IllegalStateException(ERR_MSG_INTENT)
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!started) {
            startActivityForResult(launchIntent(), requestCode())
            started = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode()) {
            InlineActivityResult
                    .getInstance()
                    .stopWithResult(requestCode = requestCode, resultCode = resultCode, data = data
                            ?: Intent())
        }
    }

    private fun launchIntent(): Intent {
        return arguments?.getParcelable(KEY_INTENT) ?: throw IllegalStateException(ERR_MSG_INTENT)
    }

    private fun requestCode(): Int {
        return arguments?.getInt(KEY_REQUEST_CODE, 0)
                ?.takeIf { it != 0 }
                ?: throw IllegalStateException(ERR_MSG_REQUEST_CODE)
    }

    companion object {
        fun newInstance(launchIntent: Intent, requestCode: Int): ActivityResultFragment {
            return ActivityResultFragment()
                    .apply {
                        arguments = Bundle().apply {
                            putParcelable(KEY_INTENT, launchIntent)
                            putInt(KEY_REQUEST_CODE, requestCode)
                        }
                    }
        }

        private const val KEY_INTENT = "key_intent"
        private const val KEY_REQUEST_CODE = "key_request_code"
        private const val KEY_STARTED = "key_started"
    }
}
