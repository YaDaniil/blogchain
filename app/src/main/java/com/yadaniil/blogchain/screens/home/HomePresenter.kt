package com.yadaniil.blogchain.screens.home

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.yadaniil.blogchain.Application
import com.yadaniil.blogchain.BuildConfig
import com.yadaniil.blogchain.data.Repository
import com.yadaniil.blogchain.data.api.models.coinmarketcap.CmcGlobalDataResponse
import com.yadaniil.blogchain.data.api.models.coinmarketcap.CmcMarketCapAndVolumeChartResponse
import com.yadaniil.blogchain.data.api.models.coinmarketcap.TickerResponse
import com.yadaniil.blogchain.data.db.models.CoinMarketCapCurrencyRealm
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by danielyakovlev on 11/2/17.
 */

@InjectViewState
class HomePresenter : MvpPresenter<HomeView>() {

    @Inject lateinit var repo: Repository

    init {
        Application.component?.inject(this)
    }

    fun getPortfolios() = repo.getAllPortfolio()
    fun getAllCoins() = repo.getAllCoinsFromDb()

    fun showChangelogDialog() {
        if (repo.getLastShowChangelogVersion() != BuildConfig.VERSION_CODE) {
            viewState.showChangelogDialog()
            repo.setLastShowChangelogVersion(BuildConfig.VERSION_CODE)
        }
    }

    class UpdateAllZipRequest(val coins: List<TickerResponse>,
                              val globalData: CmcGlobalDataResponse,
                              val chartsData: CmcMarketCapAndVolumeChartResponse)

    fun updateAll() {
        val updateAllZipRequest = Observable.zip(
                updateCoins(), updateGlobalData(), updateMarketCapAndVolumeCharts(),
                Function3<List<TickerResponse>, CmcGlobalDataResponse, CmcMarketCapAndVolumeChartResponse, UpdateAllZipRequest> {
                    tickers, cmcGlobalDataResponse, cmcChartsResponse ->
                    UpdateAllZipRequest(tickers, cmcGlobalDataResponse, cmcChartsResponse)
                }
        )

        updateAllZipRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .doOnComplete { viewState.stopLoading() }
                .subscribe({ zipRequest ->
                    repo.saveCoinsToDb(CoinMarketCapCurrencyRealm.convertApiResponseToRealmList(zipRequest.coins))
                    repo.saveCmcGlobalData(zipRequest.globalData)
                    repo.saveCmcMarketCapAndVolumeChartData(zipRequest.chartsData)
                    viewState.updateGlobalData(zipRequest.globalData)
                    viewState.updateMarketCapChart(zipRequest.chartsData)
                }, { error ->
                    viewState.stopLoading()
                    viewState.showLoadingError()
                    Timber.e(error.message)
                })
    }

    private fun updateCoins(): Observable<List<TickerResponse>> {
        return repo.getAllCoins(limit = "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun updateGlobalData(): Observable<CmcGlobalDataResponse> {
        return repo.getGlobalData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun updateMarketCapAndVolumeCharts(): Observable<CmcMarketCapAndVolumeChartResponse> {
        return repo.downloadCmcMarketCapAndVolumeCharts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun showOrHidePortfolioBalance(isPortfolioShown: Boolean? = null) {
        if(isPortfolioShown != null)
            repo.setShowPortfolioAtHome(!isPortfolioShown)

        viewState.showOrHidePortfolio(repo.getShowPortfolioAtHome())
    }

    fun setSavedGlobalData() {
        viewState.updateGlobalData(repo.getCmcGlobalData())
    }

    fun setSavedGlobalDataChart() {
        viewState.updateMarketCapChart(repo.getCmcMarketCapAndVolumeChartData())
    }
}