package com.yadaniil.app.cryptomarket.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yadaniil.app.cryptomarket.R
import com.yadaniil.app.cryptomarket.Application
import javax.inject.Inject

class MainActivity : AppCompatActivity(), IMainView {

    @Inject
    lateinit var presenter : MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerMainComponent.builder().applicationComponent(Application.component).mainModule(MainModule(this)).build().inject(this)
    }
}
