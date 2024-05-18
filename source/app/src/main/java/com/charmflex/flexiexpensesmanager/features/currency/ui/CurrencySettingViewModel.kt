package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.CurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetAllCurrencyNamesUseCase
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CurrencySettingViewModel @Inject constructor(
    private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
    private val getAllCurrencyNamesUseCase: GetAllCurrencyNamesUseCase,
    private val userCurrencyRepository: UserCurrencyRepository,
    private val currencyRepository: CurrencyRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private val _viewState = MutableStateFlow(CurrencySettingViewState())
    val viewState = _viewState.asStateFlow()

    init {
        fetchCurrencyOptions()
    }

    private fun fetchCurrencyOptions() {
        viewModelScope.launch {
            getAllCurrencyNamesUseCase().fold(
                onSuccess = { res ->
                    _viewState.update {
                        it.copy(
                            currencyOptions = res
                        )
                    }
                },
                onFailure = {}
            )
        }
    }

    fun onSearchValueChanged(searchValue: String) {
        val initialItems = _viewState.value.currencyOptions
        val updatedItems = initialItems.filter { it.contains(searchValue, ignoreCase = true) }
        _viewState.update {
            it.copy(
                bottomSheetState = it.bottomSheetState.copy(
                    items = updatedItems
                )
            )
        }
    }

    fun onLaunchCurrencySelectionBottomSheet() {
        _viewState.update {
            it.copy(
                bottomSheetState = it.bottomSheetState.copy(
                    isVisible = true,
                    items = _viewState.value.currencyOptions
                )
            )
        }
    }

    fun onCloseCurrencySelectionBottomSheet() {
        _viewState.update {
            it.copy(
                bottomSheetState = it.bottomSheetState.copy(isVisible = false)
            )
        }
    }

    fun onSecondaryCurrencySelected(newValue: String) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    bottomSheetState = it.bottomSheetState.copy(isVisible = false),
                    secondaryCurrency = newValue,
                    currencyRate = getCurrencyRateUseCase(newValue)?.toString() ?: "1"
                )
            }
        }
    }

    fun addSecondaryCurrency() {
        val currency = _viewState.value.secondaryCurrency
        val rate = _viewState.value.currencyRate.toFloatOrNull()
        if (currency.isEmpty() || rate == null) return

        viewModelScope.launch {
            toggleLoader(true)
            userCurrencyRepository.setUserSetCurrencyRate(
                currency = currency,
                rate = rate
            )
            userCurrencyRepository.addSecondaryCurrency(currency)
            routeNavigator.pop()
        }
    }

    private fun toggleLoader(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}

internal data class CurrencySettingViewState(
    val isLoading: Boolean = false,
    val mainCurrency: String = "",
    val secondaryCurrency: String = "",
    val currencyOptions: List<String> = listOf(),
    val currencyRate: String = "",
    val bottomSheetState: CurrencyLookupBottomSheetState = CurrencyLookupBottomSheetState()
) {
    data class CurrencyLookupBottomSheetState(
        val isVisible: Boolean = false,
        val items: List<String> = listOf()
    )
}