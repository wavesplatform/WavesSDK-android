package com.mysite.mywavesapplication.keeper


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mysite.mywavesapplication.R
import com.mysite.mywavesapplication.app.App
import com.mysite.mywavesapplication.utils.copyToClipboard
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.keeper.model.KeeperResult
import kotlinx.android.synthetic.main.fragment_keeper.*

/**
 * A simple [Fragment] subclass.
 */
@SuppressLint("SetTextI18n")
class KeeperFragment : Fragment() {

    private var selectedTransaction: ExampleTransaction? = null
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_keeper, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         *  You must configure dApp if you want to use Waves Keeper. Look at [App]
         *  Try to send or sign data-transaction via mobile Keeper
         */

        val transactionTypesDialog = AlertDialog.Builder(requireActivity())

        edit_transaction_type.setOnClickListener {
            transactionTypesDialog.apply {
                setTitle(getString(R.string.keeper_transaction_type_dialog_title))
                setSingleChoiceItems(
                    ExampleTransaction.getTitles(),
                    ExampleTransaction.findIndexOf(selectedTransaction)
                ) { dialog, item ->
                    val transaction = ExampleTransaction.values()[item]

                    selectedTransaction = transaction
                    edit_transaction_type.setText(transaction.title)

                    logRequest(gson.toJson(transaction.getTransaction()))

                    dialog.dismiss()
                }
            }.create().show()
        }

        text_json_log.setOnClickListener {
            activity?.copyToClipboard(text_json_log.text.toString())
        }

        button_send.setOnClickListener {
            selectedTransaction?.let { transaction ->
                WavesSdk.keeper()
                    .send(
                        requireActivity(),
                        transaction.getTransaction(),
                        object : KeeperCallback<KeeperTransactionResponse> {
                            override fun onSuccess(result: KeeperResult.Success<KeeperTransactionResponse>) {
                                logResponse(gson.toJson(result.transaction))
                            }

                            override fun onFailed(error: KeeperResult.Error) {
                                logResponse(gson.toJson(error.message))
                            }
                        }
                    )
            }
        }

        button_sign.setOnClickListener {
            selectedTransaction?.let { transaction ->
                WavesSdk.keeper()
                    .sign(
                        requireActivity(),
                        transaction.getTransaction(),
                        object : KeeperCallback<KeeperTransaction> {
                            override fun onSuccess(result: KeeperResult.Success<KeeperTransaction>) {
                                logResponse(gson.toJson(result.transaction))
                            }

                            override fun onFailed(error: KeeperResult.Error) {
                                logResponse(gson.toJson(error.message))
                            }
                        }
                    )
            }
        }
    }

    private fun logRequest(text: String?) {
        text_json_log.text = "[------------ Request ------------] \n\n $text"
    }

    private fun logResponse(text: String?) {
        text_json_log.text =
            "${text_json_log.text} \n\n[------------ Response ------------] \n\n $text"
    }

    companion object {
        fun newInstance(): KeeperFragment {
            return KeeperFragment()
        }
    }
}
