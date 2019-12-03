/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.net.service

import com.wavesplatform.sdk.model.request.data.AssetsRequest
import com.wavesplatform.sdk.model.request.data.PairRatesRequest
import com.wavesplatform.sdk.model.request.data.PairRequest
import com.wavesplatform.sdk.model.response.data.*
import io.reactivex.Observable
import retrofit2.http.*

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
     * Get a list of aliases for a given address
     */
    @GET("v0/aliases")
    fun aliases(@Query("address") address: String?): Observable<AliasesResponse>

    /**
     * Get a list of assets info from a list of IDs
     * @param ids Asset IDs array
     * @param search Assets prefix-search by the query in asset names, tickers, id
     */
    @POST("v0/assets")
    fun assets(@Body request: AssetsRequest): Observable<AssetsInfoResponse>

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
    ): Observable<LastTradesResponse>

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
    @GET("v0/matchers/{matcher}/rates")
    fun pairsRates(
        @Path("matcher") matcher: String? = null,
        @Body request: PairRatesRequest
    ): Observable<PairsRatesResponse>
}
