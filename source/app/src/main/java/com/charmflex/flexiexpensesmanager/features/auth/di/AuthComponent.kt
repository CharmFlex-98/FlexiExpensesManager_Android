package com.charmflex.flexiexpensesmanager.features.auth.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.dependency_injection.DaggerMainComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponent
import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel
import dagger.BindsInstance
import dagger.Component

@Component
internal interface AuthComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context): AuthComponent
    }

    companion object {
        fun build(appContext: Context): AuthComponent {
            return DaggerAuthComponent.factory().create(appContext)
        }
    }

    fun landingScreenViewModel(): LandingScreenViewModel
}