package com.charmflex.flexiexpensesmanager.features.currency.di

import com.charmflex.flexiexpensesmanager.features.currency.ui.CurrencySettingViewModel
import com.charmflex.flexiexpensesmanager.features.currency.ui.UserCurrencyViewModel

internal interface CurrencyInjector {
    fun currencySettingViewModel(): CurrencySettingViewModel

    fun userCurrencyViewModel(): UserCurrencyViewModel
}