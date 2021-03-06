package com.yadaniil.blogchain.screens.home

import com.arellomobile.mvp.MvpView
import com.yadaniil.blogchain.data.api.models.coinmarketcap.CmcGlobalDataResponse
import com.yadaniil.blogchain.data.api.models.coinmarketcap.CmcMarketCapAndVolumeChartResponse

/**
 * Created by danielyakovlev on 11/2/17.
 */

interface HomeView : MvpView {
    fun showChangelogDialog()
    fun showLoading()
    fun stopLoading()
    fun showLoadingError()
    fun updateGlobalData(globalData: CmcGlobalDataResponse?)
    fun showOrHidePortfolio(toShow: Boolean)
    fun updateMarketCapChart(data: CmcMarketCapAndVolumeChartResponse?)
}