package com.wavesplatform.sdk.model.response.matcher

import com.google.gson.annotations.SerializedName

/**
 *  Success:
 *  {
 *       "message": {
 *       "success": true,
 *       "message": {
 *               "version": 3,
 *               "id": "8vLJDRETEkY6G6oDSDJjucMqzSXjNrcunH4LzQZBeMTo",
 *               "sender": "3PNaua1fMrQm4TArqeTuakmY1u985CgMRk6",
 *               "senderPublicKey": "B3f8VFh6T2NGT26U7rHk2grAxn5zi9iLkg4V9uxG6C8q",
 *               "matcherPublicKey": "9cpfKN9suPNvfeUNphzxXMjcnn974eme8ZhWUjaktzU5",
 *               "assetPair": {
 *                       "amountAsset": "9AT2kEi8C4AYxV1qKxtQTVpD5i54jCPvaNNRP6VzRtYZ",
 *                       "priceAsset": "474jTeYx2r2Va35794tCScAXWJG9hU2HcgxzMowaZUnu"
 *               },
 *               "orderType": "buy",
 *               "amount": 7300,
 *               "price": 13700,
 *               "timestamp": 1611934060218,
 *               "expiration": 1614439660218,
 *               "matcherFee": 300000,
 *               "matcherFeeAssetId": null,
 *               "signature": "3kHrjYjuHrBTk25UvEu85nCqLsxXjEn9r1nqxQfbATLCJDiNLdugY1WU5JYMNn6wsJgpLJY4qYViRygbZQsCiMfB",
 *               "proofs": [
 *                       "3kHrjYjuHrBTk25UvEu85nCqLsxXjEn9r1nqxQfbATLCJDiNLdugY1WU5JYMNn6wsJgpLJY4qYViRygbZQsCiMfB"
 *               ]
 *       },
 *       "status": "OrderAccepted"
 *  }
 *
 *  Error:
 *  {
 *       "error": 9440512,
 *       "message": "The signature of order is invalid...",
 *       "template": "The signature of order {{id}} is invalid: {{details}}",
 *       "params": {
 *               "id": "EFE6uzy8t9JQtvjHnEXpPd3DbC8LpDLYkYFhwozhayDA",
 *               "details": The signature of order is invalid..."
 *       },
 *       "status": "OrderRejected",
 *       "success": false
 *       }
 *
 */
data class CreateOrderResponse(
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("success")
    var success: Boolean? = null) {

    companion object {
        const val STATUS_ORDER_ACCEPTED = "OrderAccepted"
        const val STATUS_ORDER_REJECTED = "OrderRejected"
    }
}

