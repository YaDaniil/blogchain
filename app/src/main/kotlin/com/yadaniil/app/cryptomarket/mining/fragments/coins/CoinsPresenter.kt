package com.yadaniil.app.cryptomarket.mining.fragments.coins

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.yadaniil.app.cryptomarket.Application
import com.yadaniil.app.cryptomarket.data.Repository
import com.yadaniil.app.cryptomarket.data.api.models.MiningCoin
import com.yadaniil.app.cryptomarket.data.api.models.MiningCoinsResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by danielyakovlev on 9/28/17.
 */


@InjectViewState
class CoinsPresenter : MvpPresenter<CoinsView>() {

    init {
        Application.component?.inject(this)
    }

    @Inject lateinit var repo: Repository
    var downloadedCoins: MutableList<MiningCoin> = ArrayList()

    fun downloadMiningCoins() {
        val allCoinsZipRequest =  Observable.zip(repo.getAllGpuMiningCoins(), repo.getAllAsicMiningCoins(),
                BiFunction<MiningCoinsResponse, MiningCoinsResponse, List<MiningCoin>> { gpus, asics ->
            val gpuCoins = addNameAndEquipmentTypeToCoins(gpus, "GPU")
            val asicCoins = addNameAndEquipmentTypeToCoins(asics, "ASIC")
            downloadedCoins.addAll(gpuCoins)
            downloadedCoins.addAll(asicCoins)
            downloadedCoins
        })

        allCoinsZipRequest
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ allCoins ->
                    Timber.e("All coins size: " + allCoins.size)
                    viewState.showCoins(allCoins)
                }, { error ->
                    Timber.e(error.message)
                })
    }

    private fun addNameAndEquipmentTypeToCoins(response: MiningCoinsResponse,
                                               equipmentType: String): List<MiningCoin> {
        val names = response.miningCoins.keys.toList()
        val coins = response.miningCoins.values.toList()
        Timber.e(equipmentType + " coins size: " + coins.size)

        names.forEachIndexed { index, s -> coins[index].name = s; coins[index].equipmentType = equipmentType }

        return coins
    }

    fun getAllCmcCurrencies() = repo.getAllCoinMarketCapCurrenciesFromDb()
    fun getAllCcCurrencies() = repo.getAllCryptoCompareCurrenciesFromDb()
}