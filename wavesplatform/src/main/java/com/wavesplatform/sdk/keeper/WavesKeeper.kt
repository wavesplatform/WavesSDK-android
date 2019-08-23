/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.wavesplatform.sdk.keeper.interfaces.Keeper
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.keeper.model.DApp
import com.wavesplatform.sdk.keeper.model.KeeperActionType
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.utils.isAppInstalled
import com.wavesplatform.sdk.utils.isIntentAvailable

class WavesKeeper(private var context: Context) : Keeper {

    private var callback: KeeperCallback? = null

    fun configureDApp(context: Context, dApp: DApp) {
        dApp.save(getPreferences(context))
    }

    override fun sign(activity: Activity,
                      transaction: KeeperTransaction,
                      callback: KeeperCallback) {
        processIntent(activity, KeeperActionType.SIGN, transaction, callback)
    }

    override fun send(activity: Activity,
                      transaction: KeeperTransaction,
                      callback: KeeperCallback) {
        processIntent(activity, KeeperActionType.SEND, transaction, callback)
    }

    private fun processIntent(activity: Activity,
                              type: KeeperActionType,
                              transaction: KeeperTransaction,
                              callback: KeeperCallback) {
        this.callback = callback

        if (context.isAppInstalled(WAVES_APP_PACKAGE_ID)
                && context.isIntentAvailable(WAVES_APP_KEEPER_ACTION)) {
            startKeeperActivity(activity, createParams(activity, type, transaction))
        } else {
            openAppInPlayMarket(activity)
        }
    }

    private fun createParams(activity: Activity,
                             type: KeeperActionType,
                             transaction: KeeperTransaction): Bundle {
        val bundle = Bundle().apply {
            val dApp = DApp.restore(getPreferences(activity))
            putString(KeeperKeys.DApp.NAME, dApp.name)
            putString(KeeperKeys.DApp.ICON_URL, dApp.iconUrl)
            putString(KeeperKeys.KeeperAction.ACTION_TYPE, type.type)
            putParcelable(KeeperKeys.Transaction.TRANSACTION, transaction)
        }

        return bundle
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != WAVES_APP_REQUEST_CODE || data == null) {
            return false
        }

        val result = processResult(data)
        if (resultCode != Activity.RESULT_OK || result == null || result.isError) {
            callback?.onFailed(KeeperCallback.CANCELED)
        } else {
//            result.accessToken!!.save(getPreferences(context))
//            VK.apiManager.setCredentials(result.accessToken.accessToken, result.accessToken.secret)
            callback?.onSuccess(result.data)
        }

        return true
    }

    private fun processResult(result: Intent): KeeperResult? {
        val params: MutableMap<String, String>?
        when {
            result.extras != null -> {
                // Token received via VK app
                params = HashMap()
                for (key in result.extras!!.keySet()) {
                    params[key] = result.extras!!.get(key).toString()
                }
            }
            else -> return null
        }

        return KeeperResult("test")
    }


    private fun startKeeperActivity(activity: Activity, params: Bundle) {
        val intent = Intent(WAVES_APP_KEEPER_ACTION, null).apply {
            setPackage(WAVES_APP_PACKAGE_ID)
            putExtras(params)
        }
        activity.startActivityForResult(intent, WAVES_APP_REQUEST_CODE)
    }

    private fun openAppInPlayMarket(activity: Activity) {
        val uri = Uri.parse("market://details?id=$WAVES_APP_PACKAGE_ID")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            Log.d("WavesKeeper", e.localizedMessage)
        }
    }

    private fun getPreferences(context: Context): SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val WAVES_APP_PACKAGE_ID = "com.wavesplatform.wallet"
        private const val PREFERENCE_NAME = "com.wavesplatform.wallet.keeper_prefs"
        private const val WAVES_APP_KEEPER_ACTION = "com.wavesplatform.wallet.action.KEEPER"
        private const val WAVES_APP_REQUEST_CODE = 196


    }
}