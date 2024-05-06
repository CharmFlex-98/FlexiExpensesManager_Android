package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class CurrencySettingViewModel @Inject constructor(

) : ViewModel() {
    private val _viewState = MutableStateFlow(CurrencySettingViewState())
    val viewState = _viewState.asStateFlow()

    fun onMainCurrencySelected(newValue: String) {
        _viewState.update {
            it.copy(
                mainCurrency = newValue
            )
        }
    }

    fun onSecondaryCurrencySelected(newValue: String) {
        _viewState.update {
            it.copy(
                secondaryCurrency = newValue
            )
        }
    }
}

internal data class CurrencySettingViewState(
    val mainCurrency: String = "",
    val secondaryCurrency: String = ""
)