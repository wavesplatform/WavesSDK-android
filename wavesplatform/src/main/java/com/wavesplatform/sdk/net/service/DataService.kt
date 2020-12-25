/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.net.service

import com.wavesplatform.sdk.model.request.data.AssetsRequest
import com.wavesplatform.sdk.model.request.data.PairRatesRequest
import com.wavesplatform.sdk.model.request.data.PairRequest
import com.wavesplatform.sdk.model.response.data.AliasDataResponse
import com.wavesplatform.sdk.model.response.data.AliasesResponse
import com.wavesplatform.sdk.model.response.data.AssetsInfoResponse
import com.wavesplatform.sdk.model.response.data.CandlesResponse
import com.wavesplatform.sdk.model.response.data.DataServiceMassTransferTransactionResponse
import com.wavesplatform.sdk.model.response.data.LastTradesResponseDataList
import com.wavesplatform.sdk.model.response.data.PairResponse
import com.wavesplatform.sdk.model.response.data.PairsRatesResponse
import com.wavesplatform.sdk.model.response.data.SearchPairResponse
import com.wavesplatform.sdk.model.response.data.transaction.DataMassTransferTransactionResponseWrapperList
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The goal of this service is to provide a simple
 * and convenient way to get data from Waves blockchain.
 * For more information: [WavesSdk API Swagger]({https://api.wavesplatform.com/v0/docs/)
 */
interface DataService {

    /**
     * Get address for alias
     */
    @GET("v0/aliases/{alias}")
    fun alias(@Path("alias") alias: String?): Observable<AliasDataResponse>

    /**
     * Get address for alias
     */
    @GET("v0/aliases/{alias}")
    suspend fun loadAlias(@Path("alias") alias: String?): AliasDataResponse

    /**
     * Get a list of aliases for a given address
     */
    @GET("v0/aliases")
    fun aliases(@Query("address") address: String?): Observable<AliasesResponse>

    /**
     * Search by array of addressOrAlias
     */
    @GET("v0/aliases")
    fun aliases(@Query("queries") queries: Collection<String>): Single<AliasesResponse>

    /**
     * Search by array of addressOrAlias
     */
    @GET("v0/aliases")
    suspend fun loadAliases(@Query("queries") queries: Collection<String>): AliasesResponse

    /**
     * Get a list of assets info from a list of IDs
     * @param request AssetsRequest with IDs array for many ids
     */
    @POST("v0/assets")
    fun assets(@Body request: AssetsRequest): Observable<AssetsInfoResponse>

    /**
     * Get a list of assets info from a list of IDs
     * @param request AssetsRequest with IDs array for many ids
     */
    @POST("v0/assets")
    suspend fun loadAssets(@Body request: AssetsRequest): AssetsInfoResponse

    /**
     * Get a list of assets info by search
     * @param search Assets prefix-search by the query in asset names, tickers, id
     */
    @GET("v0/assets")
    fun assets(@Query("search") search: String): Observable<AssetsInfoResponse>

    /**
     * Get pair info by amount and price assets
     * @param amountAsset Asset ID of the amount asset
     * @param priceAsset Asset ID of the price asset
     */
    @GET("v0/pairs/{amountAsset}/{priceAsset}")
    fun pairs(
        @Path("amountAsset") amountAsset: String?,
        @Path("priceAsset") priceAsset: String?,
        @Query("matcher") matcher: String? = null
    ): Observable<PairResponse>

    /**
     * DEX volume, change24, last trade price
     * 1) Get list of pairs info by serialized pairs list
     * 2) Get all list of pairs info by limit (sort by volume in WAVES)
     *
     * @param pairs Get pair info by amount and price assets
     * @param searchByAsset For searching pairs, that have the {searchByAsset} in asset names,
     * tickers, id of one asset of the pair
     * @param searchByAssets For searching pairs, that have the {searchByAssets} in asset names,
     * tickers, id of one asset of the pair
     * @param matchExactly Whether to search assets of pairs exactly or not
     * Parameter position is corresponds to asset position
     * @param limit How many pairs to await in response
     * @param matcher Matcher address
     */
    @GET("v0/pairs")
    fun pairs(
        @Query("pairs") pairs: List<String>?,
        @Query("search_by_asset") searchByAsset: String?,
        @Query("search_by_assets") searchByAssets: List<String>?,
        @Query("match_exactly") matchExactly: Boolean?,
        @Query("limit") limit: Int = 100,
        @Query("matcher") matcher: String? = null
    ): Observable<SearchPairResponse>

    /**
     * DEX volume, change24, last trade price. See pairs with Get-request
     */
    @POST("v0/pairs")
    fun pairs(@Body request: PairRequest): Observable<SearchPairResponse>

    /**
     * Get a list of exchange transactions by applying filters
     * @param amountAsset Asset ID of the amount asset
     * @param priceAsset Asset ID of the price asset
     * @param limit How many transactions to await in response. Default value : 100
     */
    @GET("v0/transactions/exchange")
    fun transactionsExchange(
        @Query("amountAsset") amountAsset: String?,
        @Query("priceAsset") priceAsset: String?,
        @Query("limit") limit: Int,
        @Query("matcher") matcher: String? = null
    ): Observable<LastTradesResponseDataList>

    /**
     * Get a list of exchange transactions by applying filters
     * @param amountAsset Asset ID of the amount asset
     * @param priceAsset Asset ID of the price asset
     * @param limit How many transactions to await in response. Default value : 100
     */
    @GET("v0/transactions/exchange")
    suspend fun getExchangeTransactions(
        @Query("amountAsset") amountAsset: String?,
        @Query("priceAsset") priceAsset: String?,
        @Query("limit") limit: Int,
        @Query("matcher") matcher: String? = null
    ): LastTradesResponseDataList

    /**
     * Get a list of mass transfer transactions by applying filters
     * @param assetId Asset ID of mass transfer
     * @param sender Address who send amount
     * @param recipient Address who receive amount
     * @param limit How many transactions to await in response. Default value : 100
     */
    @GET("v0/transactions/mass-transfer")
    fun massTransferTransactions(
        @Query("assetId") assetId: String?,
        @Query("sender") sender: String?,
        @Query("recipient") recipient: String?,
        @Query("limit") limit: Int,
        @Query("after") after: String? = null
    ): Observable<DataMassTransferTransactionResponseWrapperList>

    /**
     * Get a list of mass transfer transactions by applying filters
     * @param assetId Asset ID of mass transfer
     * @param senders List of addresses who send amount
     * @param recipient Address who receive amount
     * @param limit How many transactions to await in response. Default value : 100
     */
    @GET("v0/transactions/mass-transfer")
    fun massTransferTransactions(
        @Query("assetId") assetId: String?,
        @Query("senders") senders: List<String>?,
        @Query("recipient") recipient: String?,
        @Query("limit") limit: Int,
        @Query("after") after: String? = null
    ): Single<DataMassTransferTransactionResponseWrapperList>

    /**
     * Get candles by amount and price assets. Maximum amount of candles in response – 1440
     * @param amountAsset Asset ID of the amount asset
     * @param priceAsset Asset ID of the price asset
     * @param interval Candle interval. One of 1d, 12h, 6h, 3h, 1h, 30m, 15m, 5m, 1m
     * @param timeStart Time range filter, start
     * @param timeEnd Time range filter, end. Defaults to now
     * @param matcher Matcher address
     */
    @GET("v0/candles/{amountAsset}/{priceAsset}")
    fun candles(
        @Path("amountAsset") amountAsset: String?,
        @Path("priceAsset") priceAsset: String?,
        @Query("interval") interval: String,
        @Query("timeStart") timeStart: Long,
        @Query("timeEnd") timeEnd: Long,
        @Query("matcher") matcher: String? = null
    ): Observable<CandlesResponse>

    /**
     * Get rates info by amount and price assets
     * @param pairs Pairs list (amountAsset/priceAsset)
     * @param matcher Matcher address
     */
    @POST("v0/matchers/{matcher}/rates")
    fun pairsRates(
        @Path("matcher") matcher: String? = null,
        @Body request: PairRatesRequest
    ): Observable<PairsRatesResponse>

    /**
     * Get mass transfer transactions
     * @param sender Address-sender of the transaction
     * @param recipient Search transactions by recipient address
     * @param assetId Filter transactions by assetId
     * @param after Cursor in base64 encoding. Holds information about timestamp, id, sort
     * @param limit How many transactions to await in response
     */
    @GET("v0/transactions/mass-transfer")
    fun getMassTransferTransaction(
        @Query("sender") sender: String,
        @Query("recipient") recipient: String,
        @Query("assetId") assetId: String,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int
    ): Observable<DataServiceMassTransferTransactionResponse>

    /**
     * Get mass transfer transactions
     * @param senders List of sender addresses of the transaction
     * @param recipient Search transactions by recipient address
     * @param assetId Filter transactions by assetId
     * @param after Cursor in base64 encoding. Holds information about timestamp, id, sort
     * @param limit How many transactions to await in response
     */
    @GET("v0/transactions/mass-transfer")
    fun getMassTransferTransaction(
        @Query("senders") senders: List<String>,
        @Query("recipient") recipient: String,
        @Query("assetId") assetId: String,
        @Query("after") after: String? = null,
        @Query("limit") limit: Int
    ): Observable<DataServiceMassTransferTransactionResponse>

    /**
     * Get mass transfer transactions
     * @param senders List of sender addresses of the transaction
     * @param recipient Search transactions by recipient address
     * @param assetId Filter transactions by assetId
     * @param timeStart Time range filter, start. Defaults to first transaction's time_stamp in db (ISO-8601 or timestamp in milliseconds)
     * @param timeEnd Time range filter, end. Defaults to now (ISO-8601 or timestamp in milliseconds)
     */
    @GET("v0/transactions/mass-transfer")
    fun getMassTransferTransaction(
        @Query("senders") senders: List<String>,
        @Query("recipient") recipient: String,
        @Query("assetId") assetId: String,
        @Query("timeStart") timeStart: String,
        @Query("timeEnd") timeEnd: String
    ): Single<DataServiceMassTransferTransactionResponse>
}
