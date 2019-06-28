package com.mysite.mywavesapplication

import android.app.Application
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.Environment

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        WavesSdk.init(this)

        // or use Environment.TEST_NET for switch to Test-Net
        // WavesSdk.init(this, Environment.TEST_NET)
    }
}