package com.mysite.mywavesapplication

import android.app.Application
import com.wavesplatform.sdk.Wavesplatform

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Wavesplatform.init(this)
    }
}