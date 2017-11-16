package com.yadaniil.blogchain.screens.watchlist

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.yadaniil.blogchain.R
import com.yadaniil.blogchain.screens.base.BaseActivity
import com.yadaniil.blogchain.screens.base.CoinClickListener
import com.yadaniil.blogchain.data.db.models.CoinMarketCapCurrencyRealm
import com.yadaniil.blogchain.screens.base.CoinLongClickListener
import com.yadaniil.blogchain.screens.findcurrency.FindCurrencyActivity
import com.yadaniil.blogchain.utils.ListHelper
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_watchlist.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast


/**
 * Created by danielyakovlev on 10/31/17.
 */

class WatchlistActivity : BaseActivity(), WatchlistView, CoinClickListener, CoinLongClickListener {

    @InjectPresenter
    lateinit var presenter: WatchlistPresenter
    private val PICK_FAVOURITE_COIN_REQUEST_CODE = 0

    private var favourites: RealmResults<CoinMarketCapCurrencyRealm>? = null

    private lateinit var watchlistAdapter: WatchlistAdapter
    private lateinit var listDivider: RecyclerView.ItemDecoration

    // region Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        favourites = presenter.getRealmCurrenciesFavourite()
//        initAdMobBanner()
        initSwipeRefresh()
        initFab()
        initNoFavouritesView()
        setUpWatchlist(favourites!!)
        presenter.downloadAndSaveAllCurrencies()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FAVOURITE_COIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                presenter.addCoinToFavourite(data?.extras?.getString(FindCurrencyActivity.PICKED_COIN_SYMBOL))
                presenter.downloadAndSaveAllCurrencies()
            }
        }
    }
    // endregion Activity

    // region Init
    private fun initNoFavouritesView() {
        favourites?.addChangeListener { favourites ->
            if(favourites.isEmpty()) {
                no_items_text_view.visibility = View.VISIBLE
                swipe_refresh.visibility = View.GONE
            } else {
                no_items_text_view.visibility = View.GONE
                swipe_refresh.visibility = View.VISIBLE
            }
        }
    }

    private fun initFab() {
        fab.onClick { startActivityForResult(
                Intent(this, FindCurrencyActivity::class.java),
                PICK_FAVOURITE_COIN_REQUEST_CODE) }
    }

//    private fun initAdMobBanner() {
//        MobileAds.initialize(this, getString(R.string.admob_app_id))
//        val builder = AdRequest.Builder()
//                .addTestDevice(getString(R.string.admob_test_device))
//                .build()
//        adView.loadAd(builder)
//    }

    private fun initSwipeRefresh() {
        swipe_refresh.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        swipe_refresh.setOnRefreshListener {
            presenter.downloadAndSaveAllCurrencies()
        }
    }

    private fun setUpWatchlist(realmCurrencies: RealmResults<CoinMarketCapCurrencyRealm>) {
        watchlistAdapter = WatchlistAdapter(realmCurrencies, true, this,
                presenter.getCcRealmCurrencies(), this, this)
        watchlist_recycler_view.layoutManager = LinearLayoutManager(this)
        watchlist_recycler_view.adapter = watchlistAdapter
        watchlist_recycler_view.itemAnimator = null
        watchlist_recycler_view.setHasFixedSize(true)
        watchlist_recycler_view.removeItemDecoration(listDivider)
        watchlist_recycler_view.addItemDecoration(listDivider)


        watchlist_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    fab.hide()
                } else if (dy < 0) {
                    fab.show()
                }
            }
        })
    }
    // endregion Init

    // region View
    override fun showToolbarLoading() = smooth_progress_bar.progressiveStart()
    override fun stopToolbarLoading() = smooth_progress_bar.progressiveStop()

    override fun showLoadingError() = toast(R.string.error)

    override fun hideSwipeRefreshLoading() {
        if(swipe_refresh.isRefreshing)
            swipe_refresh.isRefreshing = false
    }

    override fun onClick(holder: ListHelper.CoinViewHolder, currencyRealm: CoinMarketCapCurrencyRealm) {
        // To coin activity
    }

    override fun onLongClick(holder: ListHelper.CoinViewHolder, currencyRealm: CoinMarketCapCurrencyRealm) {
        alert {
            title(R.string.remove_from_favourite_question)
            message("${currencyRealm.name} (${currencyRealm.symbol})")
            yesButton { presenter.removeCoinFromFavourites(currencyRealm) }
            cancelButton()
        }.show()
    }

    override fun getLayout() = R.layout.activity_watchlist
    // endregion View
}