package com.charmflex.flexiexpensesmanager.core.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.dependency_injection.modules.navigation.NavigationModule
import com.charmflex.flexiexpensesmanager.features.auth.di.AuthInjector
import com.charmflex.flexiexpensesmanager.features.expenses.di.ExpensesInjector
import com.charmflex.flexiexpensesmanager.features.expenses.di.modules.AuthModules
import com.charmflex.flexiexpensesmanager.features.expenses.di.modules.ExpensesTypeProviderModule
import com.charmflex.flexiexpensesmanager.features.expenses.ui.NewExpensesViewModel
import com.charmflex.flexiexpensesmanager.features.home.di.HomeInjector
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        NavigationModule::class,
        AuthModules::class,
    ]
)
@Singleton
internal interface AppComponent : MainInjector, AuthInjector, ExpensesInjector, HomeInjector {

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