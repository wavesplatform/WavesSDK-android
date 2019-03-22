package com.mysite.mywavesapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.wavesplatform.sdk.Wavesplatform
import com.wavesplatform.sdk.net.OnErrorListener
import com.wavesplatform.sdk.net.RetrofitException
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.getScaledAmount
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    // For Activity or Fragment add Observables in CompositeDisposable
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // 1st you must init() Wavesplatform in Application

        fab.setOnClickListener {
            // Generate or add your seed
            val newSeed = Wavesplatform.generateSeed()
            seedTextView.text = "New seed is: $newSeed"

            // Create Wallet with your seed
            Wavesplatform.createWallet(newSeed)

            // Create request to Node service about address balance
            compositeDisposable.add(
                Wavesplatform.getNodeService()
                    .wavesBalance(Wavesplatform.getAddress())
                    .compose(RxUtil.applyObservableDefaultSchedulers())
                    .subscribe({ wavesBalance ->
                        // Success
                        Toast.makeText(
                            this,
                            "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                            Toast.LENGTH_SHORT
                        ).show()
                    }, { error ->
                        // Fail
                        val errorMessage = "Can't get wavesBalance! + ${error.message}"
                        Log.e("MainActivity", errorMessage)
                        error.printStackTrace()
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    })
            )
        }

        Wavesplatform.setOnErrorListener(object : OnErrorListener {
            override fun onError(exception: RetrofitException) {
                // Handle by RetrofitException.Type
            }
        })
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
}
