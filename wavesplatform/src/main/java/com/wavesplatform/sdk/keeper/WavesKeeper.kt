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
import android.os.Parcelable
import android.support.annotation.RestrictTo
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.wavesplatform.sdk.keeper.interfaces.Keeper
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.keeper.model.DApp
import com.wavesplatform.sdk.keeper.model.KeeperActionType
import com.wavesplatform.sdk.keeper.model.KeeperProcessData
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.model.response.node.transaction.DataTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.InvokeScriptTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.TransferTransactionResponse
import com.wavesplatform.sdk.utils.isAppInstalled
import com.wavesplatform.sdk.utils.isIntentAvailable
import com.wavesplatform.sdk.utils.startActivityForResult

class WavesKeeper(private var context: Context) : Keeper {

    override fun configureDApp(context: Context,
                               dAppName: String,
                               dAppIconUrl: String) {
        DApp(dAppName, dAppIconUrl).save(getPreferences(context))
    }

    override fun <T : KeeperTransaction> sign(activity: FragmentActivity,
                                              transaction: KeeperTransaction,
                                              callback: KeeperCallback<T>) {
        processIntent(activity, KeeperActionType.SIGN, transaction, callback)
    }

    override fun <T : KeeperTransactionResponse> send(activity: FragmentActivity,
                                                      transaction: KeeperTransaction,
                                                      callback: KeeperCallback<T>) {
        processIntent(activity, KeeperActionType.SEND, transaction, callback)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun finishSign(activity: FragmentActivity, transaction: KeeperTransaction) {
        processFinish(activity, KeeperActionType.SIGN, transaction)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun finishSend(activity: FragmentActivity, transaction: KeeperTransactionResponse) {
        processFinish(activity, KeeperActionType.SEND, transaction)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun finishSign(activity: FragmentActivity, error: String) {
        processFinishWithError(activity, KeeperActionType.SIGN, error)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun finishSend(activity: FragmentActivity, error: String) {
        processFinishWithError(activity, KeeperActionType.SEND, error)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun processData(intent: Intent): KeeperProcessData? {
        if (!isKeeperIntent(intent)) {
            return null
        }

        intent.extras?.let { bundle ->
            val action = KeeperActionType.valueOf(
                    bundle.getString(KeeperKeys.ActionKeys.ACTION_TYPE)
                            ?: KeeperActionType.SIGN.name)

            val dApp = DApp(bundle.getString(KeeperKeys.DAppKeys.NAME),
                    bundle.getString(KeeperKeys.DAppKeys.ICON_URL))

            val transaction = bundle.getParcelable<KeeperTransaction>(KeeperKeys.TransactionKeys.TRANSACTION)

            return KeeperProcessData(action, dApp, transaction)
        }

        return null
    }

    override fun isKeeperIntent(intent: Intent): Boolean {
        return (intent.action == WAVES_APP_KEEPER_ACTION && intent.extras != null)
                || (intent.extras != null && intent.hasExtra(KeeperKeys.ActionKeys.ACTION_TYPE))
    }

    private fun processFinishWithError(activity: FragmentActivity,
                                       actionType: KeeperActionType,
                                       error: String) {
        activity.apply {
            val intent = Intent().apply {
                putExtras(Bundle().apply {
                    putString(KeeperKeys.ActionKeys.ACTION_TYPE, actionType.name)
                    putString(KeeperKeys.ResultKeys.ERROR_MESSAGE, error)
                })
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun <T : Parcelable> processFinish(activity: FragmentActivity, actionType: KeeperActionType, transaction: T) {
        activity.apply {
            val intent = Intent().apply {
                putExtras(Bundle().apply {
                    putString(KeeperKeys.ActionKeys.ACTION_TYPE, actionType.name)
                    putParcelable(KeeperKeys.TransactionKeys.TRANSACTION, transaction)
                })
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun <T> processIntent(activity: FragmentActivity,
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

    private fun <T> startKeeperActivity(activity: FragmentActivity, params: Bundle,
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
            putString(KeeperKeys.ActionKeys.ACTION_TYPE, type.name)
            putParcelable(KeeperKeys.TransactionKeys.TRANSACTION, transaction)
        }
    }

    private fun <T> checkAndPass(callback: KeeperCallback<T>?, result: KeeperResult?) {
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
            // Canceled flow
            result.extras == null && !success -> {
                KeeperResult.Error("Action Cancelled", KeeperResult.CANCELED)
            }
            // Success flow
            result.extras != null && !result.hasExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE) && success -> {
                val action = KeeperActionType.valueOf(
                        result.getStringExtra(KeeperKeys.ActionKeys.ACTION_TYPE)
                                ?: KeeperActionType.SIGN.name)
                when (action) {
                    KeeperActionType.SIGN -> {
                        when (val transaction =
                                result.getParcelableExtra<KeeperTransaction>(KeeperKeys.TransactionKeys.TRANSACTION)) {
                            is DataTransaction -> KeeperResult.Success(transaction)
                            is TransferTransaction -> KeeperResult.Success(transaction)
                            is InvokeScriptTransaction -> KeeperResult.Success(transaction)
                            else -> null

                        }
                    }
                    KeeperActionType.SEND -> {
                        when (val transaction =
                                result.getParcelableExtra<KeeperTransactionResponse>(KeeperKeys.TransactionKeys.TRANSACTION)) {
                            is DataTransactionResponse -> KeeperResult.Success(transaction)
                            is TransferTransactionResponse -> KeeperResult.Success(transaction)
                            is InvokeScriptTransactionResponse -> KeeperResult.Success(transaction)
                            else -> null
                        }
                    }
                }
            }
            // Error flow
            result.extras != null && result.hasExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE) && success -> {
                KeeperResult.Error(result.getStringExtra(KeeperKeys.ResultKeys.ERROR_MESSAGE), KeeperResult.UNKNOWN_ERROR)
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
        private const val WAVES_APP_PACKAGE_ID = "com.wavesplatform.wallet.dev"
        private const val PREFERENCE_NAME = "com.wavesplatform.wallet.keeper_prefs"
        private const val WAVES_APP_KEEPER_ACTION = "com.wavesplatform.wallet.action.KEEPER"
        private const val WAVES_APP_REQUEST_CODE = 196
    }
}