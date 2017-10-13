package com.yadaniil.blogchain.data.api

import com.yadaniil.blogchain.data.api.models.TickerResponse
import com.yadaniil.blogchain.utils.Endpoints
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by danielyakovlev on 7/1/17.
 */
interface CoinMarketCapService {

    @GET(Endpoints.COIN_MARKET_CAP_TICKER_ENDPOINT)
    fun getAllCurrencies(@Query("convert") convertToCurrency: String? = null,
                         @Query("limit") limit: String? = null): Observable<List<TickerResponse>>
}