package com.yadaniil.blogchain.screens.findcurrency.favourite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.yadaniil.blogchain.R
import com.yadaniil.blogchain.data.db.models.CoinMarketCapCurrencyRealm
import com.yadaniil.blogchain.screens.findcurrency.FindCurrencyActivity.Companion.PICKED_COIN_SYMBOL
import com.yadaniil.blogchain.screens.findcurrency.crypto.FindCoinAdapter
import com.yadaniil.blogchain.utils.ListHelper
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_find_favourite.*
import timber.log.Timber
import com.yadaniil.blogchain.screens.findcurrency.events.InitFavouritesSearchViewEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode



/**
 * Created by danielyakovlev on 11/15/17.
 */


class FindFavouriteFragment : MvpAppCompatFragment(), ListHelper.OnCoinClickListener, FindFavouriteView {

    @InjectPresenter lateinit var presenter: FindFavouritePresenter

    private lateinit var findCoinAdapter: FindCoinAdapter
    private lateinit var searchView: MaterialSearchView

    private lateinit var allFavouriteCoins: RealmResults<CoinMarketCapCurrencyRealm>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initCoinList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_find_favourite, container, false)

    private fun initSearchView() {
        if(searchView.isSearchOpen)
            searchView.closeSearch()

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                findCoinAdapter.updateData(presenter.getFavouriteCoinsFiltered(newText ?: ""))
                return true
            }
        })
    }

    private fun initCoinList() {
        allFavouriteCoins = presenter.getFavouriteCoins()
        allFavouriteCoins.addChangeListener { favoriteCoins ->
            if(favoriteCoins.isEmpty()) {
                favourites_recycler_view.visibility = View.GONE
                no_items_text_view.visibility = View.VISIBLE
            } else {
                if(favourites_recycler_view != null && no_items_text_view != null) {
                    favourites_recycler_view.visibility = View.VISIBLE
                    no_items_text_view.visibility = View.GONE
                }
            }
        }

        findCoinAdapter = FindCoinAdapter(allFavouriteCoins, true, this, presenter.repo, activity!!)
        favourites_recycler_view.layoutManager = LinearLayoutManager(activity)
        favourites_recycler_view.adapter = findCoinAdapter
        favourites_recycler_view.setHasFixedSize(true)
    }

    override fun onClick(holder: ListHelper.FindCoinHolder?, currencyRealm: CoinMarketCapCurrencyRealm) {
        val returnIntent = Intent()
        returnIntent.putExtra(PICKED_COIN_SYMBOL, currencyRealm.symbol)
        activity!!.setResult(Activity.RESULT_OK, returnIntent)
        activity!!.finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: InitFavouritesSearchViewEvent) {
        Timber.e(event.message)
        initSearchView()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    companion object {
        fun newInstance(search_view: MaterialSearchView): FindFavouriteFragment {
            val fragment = FindFavouriteFragment()
            fragment.searchView = search_view
            return fragment
        }
    }
}