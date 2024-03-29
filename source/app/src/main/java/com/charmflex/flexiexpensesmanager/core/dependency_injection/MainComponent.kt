package com.charmflex.flexiexpensesmanager.core.dependency_injection

import android.content.Context
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.dependency_injection.modules.NavigationModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        NavigationModule::class
    ]
)
@Singleton
interface MainComponent {
    fun routeNavigator(): RouteNavigator

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context): MainComponent
    }

    companion object {
        fun build(appContext: Context): MainComponent? {
            return DaggerMainComponent.factory().create(appContext)
        }
    }

}

interface MainComponentProvider {
    fun getMainComponent(): MainComponent
}