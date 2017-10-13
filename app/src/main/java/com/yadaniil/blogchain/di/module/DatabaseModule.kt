package com.yadaniil.blogchain.di.module

import android.content.Context
import com.yadaniil.blogchain.data.api.AppApiHelper
import com.yadaniil.blogchain.data.db.AppDbHelper
import com.yadaniil.blogchain.data.prefs.SharedPrefs
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRealm(context: Context): Realm {
        Realm.init(context)
        return Realm.getDefaultInstance()
    }

    @Singleton
    @Provides
    fun provideAppApiHelper() = AppApiHelper()

    @Singleton
    @Provides
    fun provideAppDbHelper() = AppDbHelper()

    @Singleton
    @Provides
    fun provideSharedPrefs() = SharedPrefs()
}