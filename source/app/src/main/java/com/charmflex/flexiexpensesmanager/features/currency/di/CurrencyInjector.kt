package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.features.currency.ui.CurrencySettingViewModel

internal interface CurrencyInjector {
    fun currencySettingViewModel(): CurrencySettingViewModel
}