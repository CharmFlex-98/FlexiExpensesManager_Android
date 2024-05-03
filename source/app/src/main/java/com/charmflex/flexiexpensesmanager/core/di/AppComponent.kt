package com.charmflex.flexiexpensesmanager.core.di

import android.accounts.Account
import android.content.Context
import com.charmflex.flexiexpensesmanager.db.di.modules.DBModule
import com.charmflex.flexiexpensesmanager.dependency_injection.modules.navigation.NavigationModule
import com.charmflex.flexiexpensesmanager.features.account.di.AccountInjector
import com.charmflex.flexiexpensesmanager.features.account.di.modules.AccountModule
import com.charmflex.flexiexpensesmanager.features.auth.di.AuthInjector
import com.charmflex.flexiexpensesmanager.features.category.category.di.CategoryInjector
import com.charmflex.flexiexpensesmanager.features.transactions.di.TransactionInjector
import com.charmflex.flexiexpensesmanager.features.transactions.di.modules.TransactionModule
import com.charmflex.flexiexpensesmanager.features.home.di.HomeInjector
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        NavigationModule::class,
        TransactionModule::class,
        AccountModule::class,
        DBModule::class,
        MainModule::class
    ]
)
@Singleton
internal interface AppComponent : MainInjector, AuthInjector, TransactionInjector, HomeInjector, CategoryInjector, AccountInjector {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context): AppComponent
    }

    companion object {
        fun build(appContext: Context): AppComponent {
            return DaggerAppComponent.factory().create(appContext)
        }
    }
}


internal interface AppComponentProvider {
    fun getAppComponent(): AppComponent

    companion object {
        lateinit var instance: AppComponentProvider
    }
}