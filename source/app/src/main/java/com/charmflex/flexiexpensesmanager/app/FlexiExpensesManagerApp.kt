package com.charmflex.flexiexpensesmanager.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.charmflex.flexiexpensesmanager.app.di.AppComponent
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider

internal class FlexiExpensesManagerApp : Application(), AppComponentProvider {
    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        AppComponentProvider.instance = this
        appComponent = AppComponent.build(applicationContext)
//        WorkManager.initialize(
//            this,
//            Configuration.Builder().apply {
//                appComponent?.let {
//                    setWorkerFactory(it.workerFactory())
//                }
//            }.build()
//        )
    }

    override fun getAppComponent(): AppComponent {
        return appComponent ?: throw Exception("MainComponent cannot be null!")
    }

}