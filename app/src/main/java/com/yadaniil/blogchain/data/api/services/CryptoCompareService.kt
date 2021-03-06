package com.yadaniil.blogchain.data.api.services

import com.yadaniil.blogchain.data.api.models.cryptocompare.CryptoCompareCurrenciesListResponse
import com.yadaniil.blogchain.data.api.models.cryptocompare.MinersResponse
import com.yadaniil.blogchain.utils.Endpoints
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by danielyakovlev on 7/2/17.
 */
interface CryptoCompareService {

    @GET(Endpoints.CRYPTO_COMPARE_COIN_LIST_ENDPOINT)
    fun getFullCurrenciesList(): Observable<CryptoCompareCurrenciesListResponse>

    @GET(Endpoints.CRYPTO_COMPARE_MINERS_ENDPOINT)
    fun getMiners(): Observable<MinersResponse>

}