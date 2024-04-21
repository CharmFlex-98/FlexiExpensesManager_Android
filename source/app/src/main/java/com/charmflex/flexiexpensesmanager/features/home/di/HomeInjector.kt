package com.charmflex.flexiexpensesmanager.features.home.di

import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel

internal interface HomeInjector {
    fun homeViewModel(): HomeViewModel
}