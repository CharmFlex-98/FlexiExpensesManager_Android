package com.charmflex.flexiexpensesmanager.features.auth.di

import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses.NewTransactionViewModel

internal interface AuthInjector {
    fun landingScreenViewModel(): LandingScreenViewModel
}