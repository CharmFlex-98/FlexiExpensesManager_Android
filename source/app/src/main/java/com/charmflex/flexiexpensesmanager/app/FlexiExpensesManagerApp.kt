package com.charmflex.flexiexpensesmanager.app

import android.app.Application
import androidx.collection.mutableIntListOf
import com.charmflex.flexiexpensesmanager.core.dependency_injection.MainComponent
import com.charmflex.flexiexpensesmanager.core.dependency_injection.MainComponentProvider
import com.charmflex.flexiexpensesmanager.core.network.DefaultNetworkClientBuilder

class FlexiExpensesManagerApp : Application(), MainComponentProvider {
    private var mainComponent: MainComponent? = null

    override fun onCreate() {
        super.onCreate()
        mainComponent = MainComponent.build(applicationContext)
    }

    override fun getMainComponent(): MainComponent {
        return mainComponent ?: throw Exception("MainComponent cannot be null!")
    }

}