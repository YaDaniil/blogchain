package com.yadaniil.app.blogchain.main

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.yadaniil.app.blogchain.R
import com.yadaniil.app.blogchain.base.BaseActivity
import com.yadaniil.app.blogchain.data.db.models.CoinMarketCapCurrencyRealm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), IMainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    private lateinit var listDivider: RecyclerView.ItemDecoration
    private lateinit var currenciesAdapter: CurrenciesAdapter

    // region Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listDivider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        setUpCurrenciesList(presenter.getRealmCurrencies())
        initSearchView()
        initBackgroundRefresh()
        presenter.showChangelogDialog()
    }

    override fun onBackPressed() {
        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_currencies_list, menu)
        val item = menu?.findItem(R.id.action_search)
        search_view.setMenuItem(item)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_sort) {
            CoinSorter.showCoinSortDialog(this, currenciesAdapter)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        currencies_recycler_view.adapter = null
    }
    // endregion Activity

    private fun initBackgroundRefresh() {
//        val scheduledExecutorService = Executors.newScheduledThreadPool(5)
//        scheduledExecutorService.scheduleAtFixedRate({
        presenter.downloadAndSaveAllCurrencies()
//        }, 0, 40, TimeUnit.SECONDS)
    }

    private fun initSearchView() {
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setUpCurrenciesList(presenter.getRealmCurrenciesFiltered(newText ?: ""))
                return true
            }
        })
    }

    private fun setUpCurrenciesList(realmCurrencies: RealmResults<CoinMarketCapCurrencyRealm>) {
        currenciesAdapter = CurrenciesAdapter(this, presenter)
        currencies_recycler_view.layoutManager = LinearLayoutManager(this)
        currencies_recycler_view.adapter = currenciesAdapter
        currencies_recycler_view.setHasFixedSize(true)
        currencies_recycler_view.setItemViewCacheSize(20)
        currencies_recycler_view.isDrawingCacheEnabled = true
        currencies_recycler_view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        currencies_recycler_view.removeItemDecoration(listDivider)
        currencies_recycler_view.addItemDecoration(listDivider)
        currenciesAdapter.setData(realmCurrencies)
    }

    // region View
    override fun getLayout() = R.layout.activity_main
    override fun showLoading() = smooth_progress_bar.progressiveStart()
    override fun stopLoading() = smooth_progress_bar.progressiveStop()
    override fun updateList() = setUpCurrenciesList(presenter.getRealmCurrencies())
    override fun showChangelogDialog() {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag("changelogdialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ChangelogDialog().show(ft, "changelogdialog")
    }
}
// endregion View
