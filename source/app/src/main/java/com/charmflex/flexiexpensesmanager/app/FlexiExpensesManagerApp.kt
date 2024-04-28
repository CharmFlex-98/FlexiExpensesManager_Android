package com.charmflex.flexiexpensesmanager.app

import android.app.Application
import androidx.room.RoomDatabase
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.db.AppDatabase

internal class FlexiExpensesManagerApp : Application(), AppComponentProvider {
    private var appComponent: AppComponent? = null
//    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        AppComponentProvider.instance = this
        appComponent = AppComponent.build(applicationContext)
    }

    override fun getAppComponent(): AppComponent {
        return appComponent ?: throw Exception("MainComponent cannot be null!")
    }

}