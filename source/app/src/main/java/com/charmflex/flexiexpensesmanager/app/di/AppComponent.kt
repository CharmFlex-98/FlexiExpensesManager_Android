package com.charmflex.flexiexpensesmanager.app.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.core.di.MainInjector
import com.charmflex.flexiexpensesmanager.core.di.MainModule
import com.charmflex.flexiexpensesmanager.db.di.modules.DBModule
import com.charmflex.flexiexpensesmanager.dependency_injection.modules.navigation.NavigationModule
import com.charmflex.flexiexpensesmanager.features.account.di.AccountInjector
import com.charmflex.flexiexpensesmanager.features.account.di.modules.AccountModule
import com.charmflex.flexiexpensesmanager.features.auth.di.AuthInjector
import com.charmflex.flexiexpensesmanager.features.auth.di.modules.AuthModule
import com.charmflex.flexiexpensesmanager.features.backup.di.BackupInjector
import com.charmflex.flexiexpensesmanager.features.backup.di.BackupModule
import com.charmflex.flexiexpensesmanager.features.budget.di.BudgetInjector
import com.charmflex.flexiexpensesmanager.features.budget.di.modules.BudgetModules
import com.charmflex.flexiexpensesmanager.features.category.category.di.CategoryInjector
import com.charmflex.flexiexpensesmanager.features.currency.di.CurrencyInjector
import com.charmflex.flexiexpensesmanager.features.currency.di.CurrencyModule
import com.charmflex.flexiexpensesmanager.features.transactions.di.TransactionInjector
import com.charmflex.flexiexpensesmanager.features.transactions.di.modules.TransactionModule
import com.charmflex.flexiexpensesmanager.features.home.di.HomeInjector
import com.charmflex.flexiexpensesmanager.features.scheduler.di.SchedulerInjector
import com.charmflex.flexiexpensesmanager.features.scheduler.di.modules.TransactionSchedulerModule
import com.charmflex.flexiexpensesmanager.features.session.di.SessionInjector
import com.charmflex.flexiexpensesmanager.features.tag.di.TagInjector
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        AuthModule::class,
        NavigationModule::class,
        TransactionModule::class,
        AccountModule::class,
        DBModule::class,
        MainModule::class,
        CurrencyModule::class,
        BackupModule::class,
        TransactionSchedulerModule::class,
        BudgetModules::class
    ]
)
@Singleton
internal interface AppComponent : MainInjector, AuthInjector, TransactionInjector, HomeInjector,
    CategoryInjector, AccountInjector, CurrencyInjector, TagInjector, BackupInjector,
    SchedulerInjector, SessionInjector, BudgetInjector {

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