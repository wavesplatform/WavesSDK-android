package com.mysite.mywavesapplication.keeper

import com.wavesplatform.sdk.keeper.interfaces.KeeperTransaction
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.utils.SignUtil
import com.wavesplatform.sdk.utils.WavesConstants

enum class ExampleTransaction(var title: String) : IExampleTransaction {

    SUCCESS_DATA_TRANSACTION("Data Transaction (Success)") {
        override fun getTransaction(): KeeperTransaction {
            return DataTransaction(
                mutableListOf(
                    DataTransaction.Data("key0", "string", "This is Data TX"),
                    DataTransaction.Data("key1", "integer", 100),
                    DataTransaction.Data("key2", "integer", -100),
                    DataTransaction.Data("key3", "boolean", true),
                    DataTransaction.Data("key4", "boolean", false),
                    DataTransaction.Data("key5", "binary", "SGVsbG8h")
                )
            )
        }
    },


    ERROR_DATA_TRANSACTION("Data Transaction (Send Error)") {
        override fun getTransaction(): KeeperTransaction {
            return DataTransaction(
                mutableListOf(
                    DataTransaction.Data("key0", "string", "This is Data TX"),
                    DataTransaction.Data("key1", "integer", 100),
                    DataTransaction.Data("key2", "integer", -100),
                    DataTransaction.Data("key3", "boolean", true),
                    DataTransaction.Data("key4", "boolean", "test"), // ERROR here (incorrect value)
                    DataTransaction.Data("key5", "binary", "SGVsbG8h")
                )
            )
        }
    },

    SUCCESS_TRANSFER_TRANSACTION("Transfer Transaction (Success)") {
        override fun getTransaction(): KeeperTransaction {
            return TransferTransaction(
                assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
                recipient = "3Mw9vGsQa22LGez1YRCawKswfyZskobmWDj", // only TESTNET valid address
                amount = 1,
                attachment = SignUtil.textToBase58("Hello-!"),
                feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
            )
        }
    },


    ERROR_TRANSFER_TRANSACTION("Transfer Transaction (Send Error)") {
        override fun getTransaction(): KeeperTransaction {
            return TransferTransaction(
                assetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
                recipient = "000", // ERROR here (invalid address)
                amount = 1,
                attachment = SignUtil.textToBase58("Hello-!"),
                feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY
            )
        }
    },


    SUCCESS_INVOKE_SCRIPT_TRANSACTION("Invoke Script Transaction (Success)") {
        override fun getTransaction(): KeeperTransaction {
            return InvokeScriptTransaction(
                feeAssetId = WavesConstants.WAVES_ASSET_ID_EMPTY,
                call = InvokeScriptTransaction.Call("deposit"),
                payment = mutableListOf(
                    InvokeScriptTransaction.Payment(
                        amount = 900000000,
                        assetId = null
                    )
                ),
                dApp = "3Mw9vGsQa22LGez1YRCawKswfyZskobmWDj" // only TESTNET valid address
            )
        }
    },

    ERROR_INVOKE_SCRIPT_TRANSACTION("Invoke Script Transaction (Send Error)") {
        override fun getTransaction(): KeeperTransaction {
            return InvokeScriptTransaction(
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
        }
    };

    companion object {
        fun getTitles(): Array<String> {
            return values().map { it.title }.toTypedArray()
        }

        fun findIndexOf(exampleTransaction: ExampleTransaction?): Int {
            return values().indexOf(exampleTransaction)
        }
    }
}


interface IExampleTransaction {
    fun getTransaction(): KeeperTransaction
}
