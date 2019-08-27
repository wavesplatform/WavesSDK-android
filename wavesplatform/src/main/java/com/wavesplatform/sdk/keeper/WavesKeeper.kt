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
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.wavesplatform.sdk.keeper.interfaces.Keeper
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.keeper.model.DApp
import com.wavesplatform.sdk.keeper.model.KeeperActionType
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.utils.isAppInstalled
import com.wavesplatform.sdk.utils.isIntentAvailable
import com.wavesplatform.sdk.utils.startActivityForResult

class WavesKeeper(private var context: Context) : Keeper {

    fun configureDApp(context: Context,
                      dAppName: String,
                      dAppIconUrl: String) {
        DApp(dAppName, dAppIconUrl).save(getPreferences(context))
    }

    override fun <T : KeeperTransaction> sign(activity: FragmentActivity,
                                              transaction: KeeperTransaction,
                                              callback: KeeperCallback<T>) {
        processIntent(activity, KeeperActionType.SIGN, transaction, callback)
    }

    override fun <T : KeeperTransaction> send(activity: FragmentActivity,
                                              transaction: KeeperTransaction, callback: KeeperCallback<T>) {
        processIntent(activity, KeeperActionType.SEND, transaction, callback)
    }

    private fun <T : KeeperTransaction> processIntent(activity: FragmentActivity,
                                                      type: KeeperActionType,
                                                      transaction: KeeperTransaction,
                                                      callback: KeeperCallback<T>) {
        if (context.isAppInstalled(WAVES_APP_PACKAGE_ID)
                && context.isIntentAvailable(WAVES_APP_KEEPER_ACTION)) {
            startKeeperActivity(activity, createParams(activity, type, transaction), callback)
        } else {
            openAppInPlayMarket(activity)
        }
    }

    private fun <T : KeeperTransaction> startKeeperActivity(activity: FragmentActivity, params: Bundle,
                                                            callback: KeeperCallback<T>) {
        val intent = Intent(WAVES_APP_KEEPER_ACTION, null).apply {
            setPackage(WAVES_APP_PACKAGE_ID)
            putExtras(params)
        }

        activity.startActivityForResult(intent, WAVES_APP_REQUEST_CODE) { success, data ->
            val result = processResult(data, success)
            checkAndPass(callback, result)
        }
    }

    private fun createParams(activity: Activity,
                             type: KeeperActionType,
                             transaction: KeeperTransaction): Bundle {

        return Bundle().apply {
            val dApp = DApp.restore(getPreferences(activity))
            putString(KeeperKeys.DAppKeys.NAME, dApp.name)
            putString(KeeperKeys.DAppKeys.ICON_URL, dApp.iconUrl)
            putString(KeeperKeys.ActionKeys.ACTION_TYPE, type.type)
            putParcelable(KeeperKeys.TransactionKeys.TRANSACTION, transaction)
        }
    }

    private fun <T : KeeperTransaction> checkAndPass(callback: KeeperCallback<T>?, result: KeeperResult?) {
        when (result) {
            is KeeperResult.Success<*> -> {
                callback?.onSuccess(result as KeeperResult.Success<T>)
            }
            is KeeperResult.Error -> {
                callback?.onFailed(result)
            }
        }
    }

    private fun processResult(result: Intent, success: Boolean): KeeperResult? {
        return when {
            // Success flow
            result.extras != null && !result.hasExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE) -> {
                val transaction = result.getParcelableExtra<KeeperTransaction>(KeeperKeys.TransactionKeys.TRANSACTION)
                when (transaction) {
                    is DataTransaction -> KeeperResult.Success(transaction)
                    is TransferTransaction -> KeeperResult.Success(transaction)
                    is InvokeScriptTransaction -> KeeperResult.Success(transaction)
                    else -> null

                }
            }
            // Error flow
            result.extras != null && result.hasExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE) -> {
                KeeperResult.Error(result.getStringExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE), KeeperResult.UNKNOWN_ERROR)
            }
            // Canceled flow
            result.extras == null && !success -> {
                KeeperResult.Error(result.getStringExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE), KeeperResult.CANCELED)
            }
            // Unknown flow
            else -> null
        }
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