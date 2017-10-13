package com.yadaniil.blogchain.mining.fragments.coins

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.yadaniil.blogchain.R
import com.yadaniil.blogchain.data.api.models.MiningCoin
import com.yadaniil.blogchain.utils.UiHelper
import com.yalantis.filter.animator.FiltersListItemAnimator
import kotlinx.android.synthetic.main.fragment_coins.*
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import kotlin.properties.Delegates

/**
 * Created by danielyakovlev on 9/28/17.
 */


class CoinsFragment : MvpAppCompatFragment(), CoinsView, CoinItemClickListener {

    @InjectPresenter lateinit var presenter: CoinsPresenter
    private var coinsAdapter by Delegates.notNull<CoinsAdapter>()
    private lateinit var listDivider: RecyclerView.ItemDecoration

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_coins, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listDivider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        initToolbar()
        initSearchView()
        UiHelper.changeStatusBarColor(activity, R.color.colorTabCoins)
        presenter.downloadMiningCoins()
    }

    private fun initToolbar() {
        toolbar.title = getString(R.string.coins)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                activity.onBackPressed()
                true
            }
            R.id.action_info -> {
                showMiningCoinsInfoAlert()
                super.onOptionsItemSelected(item)
            }
//            R.id.action=_filter -> showMiningCoinsFilter()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMiningCoinsInfoAlert() {
        val dialogView = activity.verticalLayout {
            textView(R.string.gpu_caps) {
                typeface = Typeface.DEFAULT_BOLD
                textSize = 16f
            }
            textView(R.string.gpu_coins_info)

            textView(R.string.asic_caps) {
                typeface = Typeface.DEFAULT_BOLD
                textSize = 16f
            }
            textView(R.string.asic_coins_info)
        }

//        activity.alert("Help information").show()
//        val dialog = FilterDialogFragment()
//        dialog.show(getFragmentManager(), "tag")
    }

    override fun showCoins(coins: List<MiningCoin>) {
        coinsAdapter = CoinsAdapter(activity, this,
                presenter.getAllCmcCurrencies(), presenter.getAllCcCurrencies())
        coins_list.layoutManager = LinearLayoutManager(activity)
        coins_list.adapter = coinsAdapter
        coins_list.setHasFixedSize(true)
        coins_list.removeItemDecoration(listDivider)
        coins_list.addItemDecoration(listDivider)
        coins_list.itemAnimator = FiltersListItemAnimator()
        coinsAdapter.setData(coins)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_mining_coins, menu)
        val item = menu?.findItem(R.id.action_search)
        search_view.setMenuItem(item)
    }

    private fun initSearchView() {
        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                showCoins(presenter.getFilteredCoins(newText ?: ""))
                return true
            }

        })
    }

    override fun onClick(holder: CoinsAdapter.CoinViewHolder, coin: MiningCoin) {

    }


    companion object {
        fun newInstance() = CoinsFragment()
    }
}