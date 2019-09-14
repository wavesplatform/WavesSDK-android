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
import com.mysite.mywavesapplication.utils.copyToClipboard
import com.wavesplatform.sdk.WavesSdk
import com.wavesplatform.sdk.keeper.interfaces.KeeperCallback
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.keeper.interfaces.KeeperTransactionResponse
import com.wavesplatform.sdk.keeper.model.KeeperResult
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants
import kotlinx.android.synthetic.main.fragment_keeper.*

/**
 * A simple [Fragment] subclass.
 */
class KeeperFragment : Fragment() {
    private val transactionTypes = arrayOf<Pair<String, KeeperTransaction>>(
        "Success Data Transaction" to DataTransaction(
            mutableListOf(
                DataTransaction.Data("key0", "string", "This is Data TX"),
                DataTransaction.Data("key1", "integer", 100),
                DataTransaction.Data("key2", "integer", -100),
                DataTransaction.Data("key3", "boolean", true),
                DataTransaction.Data("key4", "boolean", false),
                DataTransaction.Data("key5", "binary", "SGVsbG8h")
            )
        ),
        "Success Transfer Transaction" to TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3P8ys7s9r61Dapp8wZ94NBJjhmPHcBVBkMf",
            amount = 1,
            attachment = SignUtil.textToBase58("Hello-!"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
        ).apply {
            fee = WavesConstants.WAVES_MIN_FEE
        }
    )

    private var selectedTransaction: KeeperTransaction? = null
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keeper, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val transactionTypesDialog = AlertDialog.Builder(requireActivity())

        edit_transaction_type.setOnClickListener {
            transactionTypesDialog.apply {
                setTitle("Select a Transaction Type with result")
                setSingleChoiceItems(
                    transactionTypes.map { (title, _) -> title }.toTypedArray(),
                    -1
                ) { dialog, item ->
                    val (_, transaction) = transactionTypes[item]

                    selectedTransaction = transaction

                    logRequest(gson.toJson(transaction))

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
                        transaction,
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
                        transaction,
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
        text_json_log.text = "${text_json_log.text} \n\n[------------ Response ------------] \n\n $text"
    }

    companion object {
        fun newInstance(): KeeperFragment {
            return KeeperFragment()
        }
    }
}
