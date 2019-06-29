package com.mysite.mywavesapplication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.model.request.node.*
import com.wavesplatform.sdk.net.OnErrorListener
import com.wavesplatform.sdk.net.NetworkException
import com.wavesplatform.sdk.utils.RxUtil
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

        fab.setOnClickListener {
            // Generate or add your seed
            val newSeed = "tomorrow puppy car cabin treat ticket weapon soda slush camp idea mountain name erupt broom"//WavesCrypto.randomSeed()
            seedTextView.text = "New seed is: $newSeed"

            val address = WavesCrypto.addressBySeed(newSeed, WavesSdk.getEnvironment().chainId.toString())

            // Create request to Node service about address balance
            //getWavesBalance(address)

            invokeTransactionsBroadcast()
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
            WavesSdk.service().getNode()
                .wavesBalance(address)
                .compose(RxUtil.applyObservableDefaultSchedulers())
                .subscribe({ wavesBalance ->
                    // Do something on success, now we have wavesBalance.balance in satoshi in Long
                    Toast.makeText(
                        this@MainActivity,
                        "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                        Toast.LENGTH_SHORT)
                        .show()
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get wavesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                })
        )
    }

    private fun transactionsBroadcast() {

        // Creation Transfer transaction and fill it with parameters
        val transferTransaction = TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3Mq6WcupmXPVAzEB8DmXXiiT3kNFynebu6h",
            amount = 1,
            fee = WavesConstants.WAVES_MIN_FEE,
            attachment = WavesCrypto.base58encode("Hello!".toByteArray()),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY)

        // Sign transaction with seed
        transferTransaction.sign(seed = SEED)

        // Try to send transaction into Waves blockchain
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                // Do something on success, now we have wavesBalance.balance in satoshi in Long
            }, { error ->
                // Do something on fail
            }))
    }

    private fun invokeTransactionsBroadcast() {
        val args = mutableListOf(
            InvokeScriptTransaction.Arg("string", "Some string!"),
            InvokeScriptTransaction.Arg("integer", 128L),
            InvokeScriptTransaction.Arg("integer", -127L),
            InvokeScriptTransaction.Arg("boolean", true),
            InvokeScriptTransaction.Arg("boolean", false),
            InvokeScriptTransaction.Arg("binary", "base64:VGVzdA=="))

        val call = InvokeScriptTransaction.Call(
            function = "testarg",
            args = args
        )

        val payment = mutableListOf(
            InvokeScriptTransaction.Payment(
                assetId = null,
                amount = 1L))

        val tx = InvokeScriptTransaction(
            dApp = "3Mv9XDntij4ZRE1XiNZed6J74rncBpiYNDV",
            call = call,
            payment = payment)

        tx.fee = 500000L
        tx.sign(SEED)

        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(tx)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe ({
                //viewState.showNetworkError()
            }, {
                //viewState.showNetworkError()
            }))
    }

    private fun aliasTransactionsBroadcast() {

        // Creation Transfer transaction and fill it with parameters
        val transferTransaction = AliasTransaction("kisskiss")

        // Sign transaction with seed
        transferTransaction.sign(seed = SEED)

        // Try to send transaction into Waves blockchain
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                // Do something on success, now we have wavesBalance.balance in satoshi in Long
            }, { error ->
                // Do something on fail
            }))
    }

    private fun burnTransactionsBroadcast() {
        val transferTransaction = BurnTransaction("EZvjPdTR6YEpvAx2fkYGtN8vLZrWo3cYCMJ2BX8DTP9k", 1)
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.assetId)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }


    private fun leasingTransactionsBroadcast() {
        val transferTransaction = LeaseTransaction("3Mq6WcupmXPVAzEB8DmXXiiT3kNFynebu6h", 1)
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.assetId)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun cancelLeasingTransactionsBroadcast() {
        val transferTransaction = LeaseCancelTransaction("GhZVneJ9L6ZmDU3ANi4v65PrQqqt3Zpmw57EXkxkir3v")
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun dataTransactionsBroadcast() {
        val transferTransaction = DataTransaction(mutableListOf(
            DataTransaction.Data("key0", "string", "This is Data TX"),
            DataTransaction.Data("key1", "integer", 100),
            DataTransaction.Data("key2", "integer", -100),
            DataTransaction.Data("key3", "boolean", true),
            DataTransaction.Data("key4", "boolean", false),
            DataTransaction.Data("key5", "binary", "")
            ))
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun massTransferTransactionsBroadcast() {
        val transfers = mutableListOf(
            MassTransferTransaction.Transfer("3Mps7CZqB9nUbEirYyCMMoA7VbqrxLvJFSB", 1),
            MassTransferTransaction.Transfer("3Mq6WcupmXPVAzEB8DmXXiiT3kNFynebu6h", 1))
        val transferTransaction = MassTransferTransaction(
            "EZvjPdTR6YEpvAx2fkYGtN8vLZrWo3cYCMJ2BX8DTP9k",
            transfers = transfers,
            attachment = "SGVsbG8h")
        transferTransaction.fee = 200000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun issueTransactionsBroadcast() {
        val transferTransaction = IssueTransaction(
            "New Asset",
            "Details of Asset",
            100_000_000L,
            8,
            true,
            "AwZd0cYf") //  AwZd0cYf = true
        transferTransaction.fee = 100000000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }


    private fun reIssueTransactionsBroadcast() {
        val transferTransaction = ReissueTransaction(
            "BHar7QeZLmHkGqQnvBRWjyHaNKJUstYBaDrPQ64cjJL9",
            100_000_000L,
            true)
        transferTransaction.fee = 100000000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun sponsorshipTransactionsBroadcast() {
        val transferTransaction = SponsorshipTransaction(
            "BHar7QeZLmHkGqQnvBRWjyHaNKJUstYBaDrPQ64cjJL9",
            0) // 0 for cancel
        transferTransaction.fee = 100000000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun setAssetScriptTransactionsBroadcast() {
        val transferTransaction = SetAssetScriptTransaction(
            "5ukS7HXB4vsds2sLZUXUUudpd7J2gPFbGWFMzmNpSjG1",
            "") // work with without base64: AweHXCN1 = false
        transferTransaction.fee = 100000000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
    }

    private fun setScriptTransactionBroadcast() {
        val transferTransaction = SetScriptTransaction(
            null) // work with without base64: AwZd0cYf = true
        transferTransaction.fee = 100000000
        transferTransaction.sign(seed = SEED)
        compositeDisposable.add(WavesSdk.service()
            .getNode()
            .transactionsBroadcast(transferTransaction)
            .compose(RxUtil.applyObservableDefaultSchedulers())
            .subscribe({ response ->
                Log.d("TX", response.id)
            }, { error ->
                Log.d("TX", error.localizedMessage)
            }))
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
        const val SEED = "tomorrow puppy car cabin treat ticket weapon soda slush camp idea mountain name erupt broom"
    }
}
