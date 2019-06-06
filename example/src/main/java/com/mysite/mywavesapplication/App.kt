package com.mysite.mywavesapplication

import android.app.Application
import com.wavesplatform.sdk.WavesPlatform

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        WavesPlatform.init(this)
    }
}