package com.mysite.mywavesapplication.app

import android.app.Application
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.utils.Environment

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        WavesSdk.init(this, Environment.TEST_NET)

        // or use Environment.TEST_NET for switch to Test-Net
        // WavesSdk.init(this, Environment.TEST_NET)

        // You must configure dApp if you want to use Waves Keeper
        WavesSdk.keeper().configureDApp(
            this,
            "My Waves DApp",
            "https://avatars2.githubusercontent.com/u/18295288"
        )
    }
}
