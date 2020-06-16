/*
 * Created by Eduard Zaydel on 27/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.utils

import android.content.Intent
import com.wavesplatform.sdk.utils.inline.InlineActivityResult
import com.wavesplatform.sdk.utils.inline.OnActivityResult

fun androidx.fragment.app.FragmentActivity.startActivityForResult(
    intent: Intent,
    requestCode: Int = 196,
    onActivityResult: OnActivityResult
) = InlineActivityResult
    .getInstance()
    .start(
        fragmentManager = supportFragmentManager,
        intent = intent,
        requestCode = requestCode,
        onActivityResult = onActivityResult
    )
