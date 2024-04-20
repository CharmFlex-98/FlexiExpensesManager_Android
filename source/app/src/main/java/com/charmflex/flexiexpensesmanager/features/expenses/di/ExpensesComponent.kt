package com.charmflex.flexiexpensesmanager.features.expenses.di

import android.content.Context
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponent
import com.charmflex.flexiexpensesmanager.dependency_injection.MainComponentProvider
import com.charmflex.flexiexpensesmanager.features.expenses.ui.NewExpensesViewModel
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [
        MainComponent::class
    ]
)
internal interface ExpensesComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context,
            mainComponent: MainComponent
        ): ExpensesComponent
    }

    companion object {
        fun build(appContext: Context): ExpensesComponent {
            return DaggerExpensesComponent.factory().create(appContext, MainComponentProvider.instance.getMainComponent())
        }
    }

    fun newExpensesViewModel(): NewExpensesViewModel
}