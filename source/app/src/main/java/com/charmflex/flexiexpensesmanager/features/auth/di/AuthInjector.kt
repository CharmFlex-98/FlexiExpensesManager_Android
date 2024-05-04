package com.charmflex.flexiexpensesmanager.features.auth.di

import com.charmflex.flexiexpensesmanager.features.auth.ui.landing.LandingScreenViewModel

internal interface AuthInjector {
    fun landingScreenViewModel(): LandingScreenViewModel
}