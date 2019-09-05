package com.mysite.mywavesapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.model.response.node.transaction.TransferTransactionResponse
import com.wavesplatform.sdk.net.NetworkException
import com.wavesplatform.sdk.net.OnErrorListener
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants
import com.wavesplatform.sdk.utils.getScaledAmount
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    // For Activity or Fragment add Observables in CompositeDisposable
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // First you must init() WavesSdk in Application and add Internet permission
        // Put your seed in const [MainActivity.Companion.SEED] from https://testnet.wavesplatform.com
        // or https://client.wavesplatform.com

        fab.setOnClickListener {
            // Generate or add your seed
            val newSeed = WavesCrypto.randomSeed()
            seedTextView.text = "New seed is: $newSeed"

            val address =
                WavesCrypto.addressBySeed(SEED, WavesSdk.getEnvironment().chainId.toString())

            // Create request to Node service about address balance
            getWavesBalance(address)

            // Examples of transactions available in [WavesServiceTest]
        }



        // You must configure dApp if you want to use Waves Keeper. Look at App
        // Try to send or sign data-transaction via mobile Keeper

        val dataTransaction = DataTransaction(mutableListOf(
            DataTransaction.Data("key0", "string", "This is Data TX"),
            DataTransaction.Data("key1", "integer", 100),
            DataTransaction.Data("key2", "integer", -100),
            DataTransaction.Data("key3", "boolean", true),
            DataTransaction.Data("key4", "boolean", false),
            DataTransaction.Data("key5", "binary", "SGVsbG8h")))

        val transaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3P8ys7s9r61Dapp8wZ94NBJjhmPHcBVBkMf",
            amount = 1,
            attachment = SignUtil.textToBase58("Hello-!"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
        )
        transaction.fee = WavesConstants.WAVES_MIN_FEE

        fab_d_app_send.setOnClickListener {
            WavesSdk.keeper()
                .send(this, transaction, object : KeeperCallback<TransferTransactionResponse> {
                    override fun onSuccess(result: KeeperResult.Success<TransferTransactionResponse>) {
                        Log.d("KEEPERTEST", result.toString())
                        Log.d("KEEPERTEST", "TXID: " + result.transaction?.id)
                    }

                    override fun onFailed(error: KeeperResult.Error) {
                        Log.d("KEEPERTEST", error.toString())
                    }
                })
        }

        fab_d_app_sign.setOnClickListener {
            WavesSdk.keeper().sign(this, transaction, object : KeeperCallback<TransferTransaction> {
                override fun onSuccess(result: KeeperResult.Success<TransferTransaction>) {
                    Log.d("KEEPERTEST", result.toString())
                }

                override fun onFailed(error: KeeperResult.Error) {
                    Log.d("KEEPERTEST", error.toString())
                }
            })
        }

        handleServiceErrors()
    }

    private fun handleServiceErrors() {
        WavesSdk.service().addOnErrorListener(object : OnErrorListener {
            override fun onError(exception: NetworkException) {
                // Handle NetworkException here
            }
        })
    }

    private fun getWavesBalance(address: String) {
        compositeDisposable.add(
            WavesSdk.service()
                .getNode() // You can choose different Waves services: node, matcher and data service
                .addressesBalance(address) // Here methods of service
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long
                    Toast.makeText(
                        this@MainActivity,
                        "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get addressesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                })
        )
    }

    // Unsubscribe after destroy
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val SEED = ""
    }
}
