package com.charmflex.flexiexpensesmanager.features.auth.di

import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel
import com.charmflex.flexiexpensesmanager.features.expenses.ui.NewExpensesViewModel

internal interface AuthInjector {
    fun newExpensesViewModel(): NewExpensesViewModel
    fun landingScreenViewModel(): LandingScreenViewModel
}