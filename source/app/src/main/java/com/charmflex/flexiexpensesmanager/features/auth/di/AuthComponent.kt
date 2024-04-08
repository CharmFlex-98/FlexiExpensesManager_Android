package com.charmflex.flexiexpensesmanager.features.auth.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.dependency_injection.AppComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponentProvider
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope


@Component(
    dependencies = [MainComponent::class]
)
@MyScope
internal interface AuthComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context, mainComponent: MainComponent): AuthComponent
    }

    companion object {
        fun build(appContext: Context): AuthComponent {
            return DaggerAuthComponent.factory().create(appContext, MainComponentProvider.instance.getMainComponent())
        }
    }

    fun landingScreenViewModel(): LandingScreenViewModel
}


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class MyScope