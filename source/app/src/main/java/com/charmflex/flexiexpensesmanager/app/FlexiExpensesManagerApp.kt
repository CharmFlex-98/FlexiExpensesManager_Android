package com.charmflex.flexiexpensesmanager.app

import android.app.Application
import com.charmflex.flexiexpensesmanager.dependency_injection.AppComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponentProvider

class FlexiExpensesManagerApp : Application(), MainComponentProvider {
    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        MainComponentProvider.instance = this
        appComponent = AppComponent.build(applicationContext)
    }

    override fun getMainComponent(): MainComponent {
        return appComponent ?: throw Exception("MainComponent cannot be null!")
    }

}