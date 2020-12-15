/*
 * Created by Eduard Zaydel on 1/4/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.net.service

import com.wavesplatform.sdk.model.request.node.AliasTransaction
import com.wavesplatform.sdk.model.request.node.BurnTransaction
import com.wavesplatform.sdk.model.request.node.DataTransaction
import com.wavesplatform.sdk.model.request.node.InvokeScriptTransaction
import com.wavesplatform.sdk.model.request.node.IssueTransaction
import com.wavesplatform.sdk.model.request.node.LeaseCancelTransaction
import com.wavesplatform.sdk.model.request.node.LeaseTransaction
import com.wavesplatform.sdk.model.request.node.MassTransferTransaction
import com.wavesplatform.sdk.model.request.node.ReissueTransaction
import com.wavesplatform.sdk.model.request.node.SetAssetScriptTransaction
import com.wavesplatform.sdk.model.request.node.SetScriptTransaction
import com.wavesplatform.sdk.model.request.node.SponsorshipTransaction
import com.wavesplatform.sdk.model.request.node.TransferTransaction
import com.wavesplatform.sdk.model.response.node.AddressAssetBalanceResponse
import com.wavesplatform.sdk.model.response.node.AssetBalancesResponse
import com.wavesplatform.sdk.model.response.node.AssetsDetailsResponse
import com.wavesplatform.sdk.model.response.node.BlockChainData
import com.wavesplatform.sdk.model.response.node.HeightResponse
import com.wavesplatform.sdk.model.response.node.HistoryTransactionResponse
import com.wavesplatform.sdk.model.response.node.IssueTransactionResponse
import com.wavesplatform.sdk.model.response.node.ScriptInfoResponse
import com.wavesplatform.sdk.model.response.node.UtilsTimeResponse
import com.wavesplatform.sdk.model.response.node.WavesBalanceResponse
import com.wavesplatform.sdk.model.response.node.transaction.AliasTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.BurnTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.DataTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.InvokeScriptTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.LeaseCancelTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.LeaseTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.MassTransferTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.ReissueTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.SetAssetScriptTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.SetScriptTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.SponsorshipTransactionResponse
import com.wavesplatform.sdk.model.response.node.transaction.TransferTransactionResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Random

/**
 * Service for working with nodes.
 * For more information: [Nodes Swagger]({https://nodes.wavesnodes.com/api-docs/index.html#!/assets/balances)
 * Any transactions you can check at [Waves Explorer]({https://wavesexplorer.com/)
 */
interface NodeService {

    /**
     * Account's Waves balance
     * @param address Address
     */
    @GET("addresses/balance/{address}")
    fun addressesBalance(@Path("address") address: String?): Observable<WavesBalanceResponse>

    /**
     * Account's Waves balance
     * @param address Address
     */
    @GET("addresses/balance/{address}")
    suspend fun addressBalance(@Path("address") address: String?): WavesBalanceResponse

    /**
     * Account's script additional info
     * @param address Address
     */
    @GET("addresses/scriptInfo/{address}")
    fun scriptInfo(@Path("address") address: String): Observable<ScriptInfoResponse>

    /**
     * Account's balances for all assets by address
     * @param address Address
     */
    @GET("assets/balance/{address}")
    fun assetsBalance(@Path("address") address: String?): Observable<AssetBalancesResponse>

    /**
     * Account's balances for all assets by address
     * @param address Address
     */
    @GET("assets/balance/{address}")
    suspend fun loadAssetBalances(@Path("address") address: String?): AssetBalancesResponse

    @GET("assets/balance/{address}")
    fun assetsBalanceWithoutCache(
        @Path("address") address: String?,
        @Query("r") random: Int? = Random().nextInt()
    ): Observable<AssetBalancesResponse>

    /**
     * Account's assetId balance by address
     * @param address Address
     * @param assetId AssetId
     */
    @GET("assets/balance/{address}/{assetId}")
    fun assetsBalance(
        @Path("address") address: String?,
        @Path("assetId") assetId: String?
    ): Observable<AddressAssetBalanceResponse>

    /**
     * Provides detailed information about given asset
     * @param assetId Asset Id
     */
    @GET("/assets/details/{assetId}")
    fun assetDetails(@Path("assetId") assetId: String): Observable<AssetsDetailsResponse>

    /**
     * Read all data posted by an account
     * @param address Account address
     * @param key Exact keys to query
     */
    @GET("/addresses/data/{address}")
    fun dataByAddress(
        @Path("address") address: String,
        @Query("key") key: String?
    ): Single<MutableList<BlockChainData>>

    /**
     * Get list of transactions where specified address has been involved
     * @param address Address
     * @param limit Number of transactions to be returned. Max is last 1000.
     */
    @GET("transactions/address/{address}/limit/{limit}")
    fun transactionsAddress(
        @Path("address") address: String?,
        @Path("limit") limit: Int
    ): Observable<List<List<HistoryTransactionResponse>>>

    /**
     * Get list of transactions where specified address has been involved
     * @param address Address
     * @param limit Number of transactions to be returned. Max is last 1000.
     */
    @GET("transactions/address/{address}/limit/{limit}")
    fun transactionsAddress(
        @Path("address") address: String?,
        @Path("limit") limit: Int,
        @Query("after") after: String? = null
    ): Single<List<List<HistoryTransactionResponse>>>

    /**
     * Get current Waves block-chain height
     */
    @GET("blocks/height")
    fun blockHeight(): Observable<HeightResponse>

    /**
     * Active leasing transactions of account
     * @param address Address
     */
    @GET("leasing/active/{address}")
    fun leasingActive(@Path("address") address: String?): Observable<List<HistoryTransactionResponse>>

    /**
     * Active leasing transactions of account
     * @param address Address
     */
    @GET("leasing/active/{address}")
    suspend fun loadActiveLeasing(@Path("address") address: String?): List<HistoryTransactionResponse>

    /**
     * Current Node time (UTC)
     */
    @GET("/utils/time")
    fun utilsTime(): Observable<UtilsTimeResponse>

    // Broadcast transactions //////////////////////////////////////

    /**
     * Broadcast issue-transaction (typeId = 3)
     * @param transaction IssueTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: IssueTransaction): Observable<IssueTransactionResponse>

    /**
     * Broadcast transfer-transaction (typeId = 4)
     * @param transaction TransferTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: TransferTransaction): Observable<TransferTransactionResponse>

    /**
     * Broadcast reissue-transaction (typeId = 5)
     * @param transaction ReissueTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: ReissueTransaction): Observable<ReissueTransactionResponse>

    /**
     * Broadcast burn-transaction (typeId = 6)
     * @param transaction BurnTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: BurnTransaction): Observable<BurnTransactionResponse>

    /**
     * Broadcast exchange-transaction (typeId = 7)
     * @param transaction ExchangeTransaction with signature by privateKey
     *//*
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: ExchangeTransaction): Observable<ExchangeTransactionResponse>*/

    /**
     * Broadcast lease-transaction (typeId = 8)
     * @param transaction LeaseTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: LeaseTransaction): Observable<LeaseTransactionResponse>

    /**
     * Broadcast lease-cancel-transaction (typeId = 9)
     * @param transaction LeaseCancelTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: LeaseCancelTransaction): Observable<LeaseCancelTransactionResponse>

    /**
     * Create alias-transaction. Alias - short name for address  (typeId = 10)
     * @param transaction AliasTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: AliasTransaction): Observable<AliasTransactionResponse>

    /**
     * Broadcast mass-transfer-transaction (typeId = 11)
     * @param transaction MassTransferTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: MassTransferTransaction): Observable<MassTransferTransactionResponse>

    /**
     * Broadcast data-transaction (typeId = 12)
     * @param transaction DataTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: DataTransaction): Observable<DataTransactionResponse>

    /**
     * Broadcast set-script-transaction, also called address-script  (typeId = 13)
     * @param transaction SetScriptTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: SetScriptTransaction): Observable<SetScriptTransactionResponse>

    /**
     * Broadcast sponsorship-transaction  (typeId = 14)
     * @param transaction SponsorshipTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: SponsorshipTransaction): Observable<SponsorshipTransactionResponse>

    /**
     * Broadcast set-asset-script-transaction (typeId = 15)
     * @param transaction SetAssetScriptTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: SetAssetScriptTransaction): Observable<SetAssetScriptTransactionResponse>

    /**
     * Broadcast invoke-script-transaction (typeId = 16)
     * @param transaction InvokeScriptTransaction with signature by privateKey
     */
    @POST("transactions/broadcast")
    fun transactionsBroadcast(@Body transaction: InvokeScriptTransaction): Observable<InvokeScriptTransactionResponse>
}
