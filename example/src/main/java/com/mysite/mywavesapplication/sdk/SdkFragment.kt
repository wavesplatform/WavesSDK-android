package com.mysite.mywavesapplication.sdk


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.text.HtmlCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mysite.mywavesapplication.R
import com.mysite.mywavesapplication.utils.copyToClipboard
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.crypto.WavesCrypto
import com.wavesplatform.sdk.net.NetworkException
import com.wavesplatform.sdk.net.OnErrorListener
import com.wavesplatform.sdk.utils.RxUtil
import com.wavesplatform.sdk.utils.getScaledAmount
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_sdk.*


/**
 * A simple [Fragment] subclass.
 */
@SuppressLint("SetTextI18n")
class SdkFragment : Fragment() {

    // For Activity or Fragment add Observables in CompositeDisposable
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_sdk, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
        * First you must init() WavesSdk in [App] and add Internet permission
        * Put your seed in const [MainActivity.Companion.SEED] from https://testnet.wavesplatform.com
        * or https://client.wavesplatform.com
         */

        button_generate_seed.setOnClickListener {
            // Generate or add your seed
            val newSeed = WavesCrypto.randomSeed()
            text_seed.text = HtmlCompat.fromHtml(
                "<b>New seed is:</b> $newSeed\"",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        button_load_balance.setOnClickListener {
            val address =
                WavesCrypto.addressBySeed(
                    SEED, // load with hardcoded seed
                    WavesSdk.getEnvironment().chainId.toString()
                )
            // Create request to Node service about address balance
            getWavesBalance(address)
            // Examples of transactions available in [WavesServiceTest]
        }

        text_seed.setOnClickListener {
            activity?.copyToClipboard(text_seed.text.toString())
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
                        requireActivity(),
                        "Balance is : ${getScaledAmount(wavesBalance.balance, 8)} Waves",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }, { error ->
                    // Do something on fail
                    val errorMessage = "Can't get addressesBalance! + ${error.message}"
                    Log.e("MainActivity", errorMessage)
                    error.printStackTrace()
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
                })
        )


    }

    // Unsubscribe after destroy
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        const val SEED = ""

        fun newInstance(): SdkFragment {
            return SdkFragment()
        }
    }
}
