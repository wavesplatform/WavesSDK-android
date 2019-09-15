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
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants
import kotlinx.android.synthetic.main.fragment_keeper.*

/**
 * A simple [Fragment] subclass.
 */
@SuppressLint("SetTextI18n")
class KeeperFragment : Fragment() {
    private val transactionTypes = arrayOf<Pair<String, KeeperTransaction>>(
        "Data Transaction (Success)" to DataTransaction(
            mutableListOf(
                DataTransaction.Data("key0", "string", "This is Data TX"),
                DataTransaction.Data("key1", "integer", 100),
                DataTransaction.Data("key2", "integer", -100),
                DataTransaction.Data("key3", "boolean", true),
                DataTransaction.Data("key4", "boolean", false),
                DataTransaction.Data("key5", "binary", "SGVsbG8h")
            )
        ),
        "Data Transaction (Send Error)" to DataTransaction(
            mutableListOf(
                DataTransaction.Data("key0", "string", "This is Data TX"),
                DataTransaction.Data("key1", "integer", 100),
                DataTransaction.Data("key2", "integer", -100),
                DataTransaction.Data("key3", "boolean", true),
                DataTransaction.Data("key4", "boolean", "test"), // ERROR here (incorrect value)
                DataTransaction.Data("key5", "binary", "SGVsbG8h")
            )
        ),
        "Transfer Transaction (Success)" to TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "3Mw9vGsQa22LGez1YRCawKswfyZskobmWDj", // only TESTNET valid address
            amount = 1,
            attachment = SignUtil.textToBase58("Hello-!"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
        ),
        "Transfer Transaction (Send Error)" to TransferTransaction(
            assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            recipient = "000", // ERROR here (invalid address)
            amount = 1,
            attachment = SignUtil.textToBase58("Hello-!"),
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
        ),
        "Invoke Script Transaction (Success)" to InvokeScriptTransaction(
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            call = InvokeScriptTransaction.Call("deposit"),
            payment = mutableListOf(
                InvokeScriptTransaction.Payment(
                    amount = 900000000,
                    assetId = null
                )
            ),
            dApp = "3Mw9vGsQa22LGez1YRCawKswfyZskobmWDj" // only TESTNET valid address
        ),
        "Invoke Script Transaction (Send Error)" to InvokeScriptTransaction(
            feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
            call = InvokeScriptTransaction.Call("deposit32"), // ERROR here (invalid function name)
            payment = mutableListOf(
                InvokeScriptTransaction.Payment(
                    amount = 900000000,
                    assetId = null
                )
            ),
            dApp = "3Mw9vGsQa22LGez1YRCawKswfyZskobmWDj" // only TESTNET valid address
        )
    )

    private var selectedTransaction: KeeperTransaction? = null
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

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
                setTitle("Select a Transaction Type with result")
                setSingleChoiceItems(
                    transactionTypes.map { (title, _) -> title }.toTypedArray(),
                    transactionTypes.indexOfFirst { (_, transaction) -> transaction == selectedTransaction }
                ) { dialog, item ->
                    val (title, transaction) = transactionTypes[item]

                    selectedTransaction = transaction
                    edit_transaction_type.setText(title)

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
        text_json_log.text =
            "${text_json_log.text} \n\n[------------ Response ------------] \n\n $text"
    }

    companion object {
        fun newInstance(): KeeperFragment {
            return KeeperFragment()
        }
    }
}
