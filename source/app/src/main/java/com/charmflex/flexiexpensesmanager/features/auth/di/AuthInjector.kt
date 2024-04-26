package com.charmflex.flexiexpensesmanager.features.auth.di

import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel
import com.charmflex.flexiexpensesmanager.features.expenses.ui.new_expenses.NewExpensesViewModel

internal interface AuthInjector {
    fun newExpensesViewModel(): NewExpensesViewModel
    fun landingScreenViewModel(): LandingScreenViewModel
}