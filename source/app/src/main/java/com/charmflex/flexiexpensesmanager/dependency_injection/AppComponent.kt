package com.charmflex.flexiexpensesmanager.dependency_injection

import android.content.Context
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.dependency_injection.modules.navigation.NavigationModule
import com.charmflex.flexiexpensesmanager.features.auth.di.DaggerAuthComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(
    modules = [
        NavigationModule::class
    ]
)
@Singleton
interface AppComponent : MainComponent {

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

interface MainComponent {
    fun routeNavigator(): RouteNavigator
}


interface MainComponentProvider {
    fun getMainComponent(): MainComponent
    companion object {
        lateinit var instance: MainComponentProvider
    }
}