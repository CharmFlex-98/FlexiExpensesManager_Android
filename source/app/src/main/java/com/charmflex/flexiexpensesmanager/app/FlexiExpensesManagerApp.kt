package com.charmflex.flexiexpensesmanager.app

import android.app.Application
import com.charmflex.flexiexpensesmanager.core.di.AppComponent
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider

internal class FlexiExpensesManagerApp : Application(), AppComponentProvider {
    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        AppComponentProvider.instance = this
        appComponent = AppComponent.build(applicationContext)
    }

    override fun getAppComponent(): AppComponent {
        return appComponent ?: throw Exception("MainComponent cannot be null!")
    }

}