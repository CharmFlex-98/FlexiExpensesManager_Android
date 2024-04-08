package com.charmflex.flexiexpensesmanager.features.home.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel
import dagger.BindsInstance
import dagger.Component

@Component
internal interface HomeComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance appContext: Context): HomeComponent
    }

    companion object {
        fun build(appContext: Context): HomeComponent {
            return DaggerHomeComponent.factory().create(appContext)
        }
    }

    fun homeViewModel(): HomeViewModel
}