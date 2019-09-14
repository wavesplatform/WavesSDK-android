package com.mysite.mywavesapplication

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mysite.mywavesapplication.keeper.KeeperFragment
import com.mysite.mywavesapplication.sdk.SdkFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//
//        // You must configure dApp if you want to use Waves Keeper. Look at App
//        // Try to send or sign data-transaction via mobile Keeper
//
//        val dataTransaction = DataTransaction(
//            mutableListOf(
//                DataTransaction.Data("key0", "string", "This is Data TX"),
//                DataTransaction.Data("key1", "integer", 100),
//                DataTransaction.Data("key2", "integer", -100),
//                DataTransaction.Data("key3", "boolean", true),
//                DataTransaction.Data("key4", "boolean", false),
//                DataTransaction.Data("key5", "binary", "SGVsbG8h")
//            )
//        )
//
//        val transaction = TransferTransaction(
//            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
//            recipient = "3P8ys7s9r61Dapp8wZ94NBJjhmPHcBVBkMf",
//            amount = 1,
//            attachment = SignUtil.textToBase58("Hello-!"),
//            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
//        )
//        transaction.fee = WavesConstants.WAVES_MIN_FEE
//
//        fab_d_app_send.setOnClickListener {
//            WavesSdk.keeper()
//                .send(this, transaction, object : KeeperCallback<TransferTransactionResponse> {
//                    override fun onSuccess(result: KeeperResult.Success<TransferTransactionResponse>) {
//                        Log.d("KEEPERTEST", result.toString())
//                        Log.d("KEEPERTEST", "TXID: " + result.transaction?.id)
//                    }
//
//                    override fun onFailed(error: KeeperResult.Error) {
//                        Log.d("KEEPERTEST", error.toString())
//                    }
//                })
//        }
//
//        fab_d_app_sign.setOnClickListener {
//            WavesSdk.keeper().sign(this, transaction, object : KeeperCallback<TransferTransaction> {
//                override fun onSuccess(result: KeeperResult.Success<TransferTransaction>) {
//                    Log.d("KEEPERTEST", result.toString())
//                }
//
//                override fun onFailed(error: KeeperResult.Error) {
//                    Log.d("KEEPERTEST", error.toString())
//                }
//            })
//        }


        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val sdkFragment = SdkFragment.newInstance()
        val keeperFragment = KeeperFragment.newInstance()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_keeper -> {
                    showFragment(keeperFragment, R.string.bottom_navigation_keeper_title)
                }
                R.id.action_sdk -> {
                    showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
                }
                else -> {
                    showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        showFragment(sdkFragment, R.string.bottom_navigation_sdk_title)
    }

    private fun showFragment(fragment: Fragment, @StringRes title: Int) {
        toolbar.setTitle(title)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frame_fragment_container, fragment)
            .commit()
    }
}
