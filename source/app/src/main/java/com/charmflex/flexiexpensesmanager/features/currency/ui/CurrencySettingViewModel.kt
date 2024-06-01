package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.popWithHomeRefresh
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CurrencyRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
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
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private val _viewState = MutableStateFlow(CurrencySettingViewState())
    val viewState = _viewState.asStateFlow()

    private var _flowType: CurrencySettingViewState.FlowType? = null

    init {
        fetchCurrencyOptions()
    }

    fun initialise(
        flowType: String
    ) {
        viewModelScope.launch {
            _flowType = when (flowType) {
                CurrencyRoutes.Args.CURRENCY_TYPE_MAIN -> CurrencySettingViewState.FlowType.PrimaryCurrencySetting(
                    initialCurrency = userCurrencyRepository.getPrimaryCurrency()
                )
                CurrencyRoutes.Args.CURRENCY_TYPE_SECONDARY_EDIT -> CurrencySettingViewState.FlowType.EditSecondaryCurrency
                else -> CurrencySettingViewState.FlowType.AddSecondaryCurrency
            }

            if (_flowType is CurrencySettingViewState.FlowType.PrimaryCurrencySetting) {
                _viewState.update {
                    it.copy(
                        currencyName = userCurrencyRepository.getPrimaryCurrency()
                    )
                }
            }
        }
    }

    fun isMainCurrencyType(): Boolean {
        return _flowType is CurrencySettingViewState.FlowType.PrimaryCurrencySetting
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

    fun onCurrencySelected(newValue: String) {
        when (_flowType) {
            null -> {}

            is CurrencySettingViewState.FlowType.PrimaryCurrencySetting -> {
                onPrimaryCurrencySelected(newValue)
            }

            else -> onSecondaryCurrencySelected(newValue)
        }
    }

    private fun onPrimaryCurrencySelected(newValue: String) {
        _viewState.update {
            it.copy(
                bottomSheetState = it.bottomSheetState.copy(isVisible = false),
                currencyName = newValue,
            )
        }
    }

    private fun onSecondaryCurrencySelected(newValue: String) {
        viewModelScope.launch {
            val currencyRate = getCurrencyRateUseCase(newValue)
            _viewState.update {
                it.copy(
                    bottomSheetState = it.bottomSheetState.copy(isVisible = false),
                    currencyName = newValue,
                    currencyRate = currencyRate?.rate?.toString() ?: "1",
                    isCustom = currencyRate?.isCustom ?: false
                )
            }
        }
    }

    fun addCurrency() {
        when (val type = _flowType) {
            null -> {}

            is CurrencySettingViewState.FlowType.PrimaryCurrencySetting -> {
                setPrimaryCurrency(type)
            }

            else -> {
                addSecondaryCurrency()
            }
        }
    }

    private fun setPrimaryCurrency(flowType: CurrencySettingViewState.FlowType.PrimaryCurrencySetting) {
        val currency = _viewState.value.currencyName

        if (flowType.initialCurrency == currency) return

        if (currency.isNotBlank()) {
            viewModelScope.launch {
                userCurrencyRepository.setPrimaryCurrency(currency)
                routeNavigator.popWithHomeRefresh()
            }
        }
    }

    fun addSecondaryCurrency() {
        val currency = _viewState.value.currencyName
        val rate = _viewState.value.currencyRate.toFloatOrNull()
        if (currency.isEmpty() || rate == null) return

        viewModelScope.launch {
            toggleLoader(true)
            if (_viewState.value.isCustom) {
                userCurrencyRepository.setUserSetCurrencyRate(
                    currency = currency,
                    rate = rate
                )
            } else {
                userCurrencyRepository.removeUserSetCurrencyRate(currency)
            }
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

    fun toggleCustomCurrency() {
        val currency = _viewState.value.currencyName
        val isCustom = !_viewState.value.isCustom
        viewModelScope.launch {
            val currencyRate = getCurrencyRateUseCase(currency, customFirst = isCustom)
            _viewState.update {
                it.copy(
                    isCustom = currencyRate?.isCustom ?: false,
                    currencyRate = currencyRate?.rate?.toString() ?: "1",
                )
            }
        }
    }

    fun onCurrencyRateChanged(newRate: String) {
        if (_viewState.value.isCustom.not()) return

        _viewState.update {
            it.copy(
                currencyRate = newRate
            )
        }
    }
}

internal data class CurrencySettingViewState(
    val isLoading: Boolean = false,
    val currencyName: String = "",
    val currencyOptions: List<String> = listOf(),
    val currencyRate: String = "",
    val isCustom: Boolean = false,
    val bottomSheetState: CurrencyLookupBottomSheetState = CurrencyLookupBottomSheetState()
) {
    data class CurrencyLookupBottomSheetState(
        val isVisible: Boolean = false,
        val items: List<String> = listOf()
    )

    sealed interface FlowType {
        data class PrimaryCurrencySetting(
            val initialCurrency: String
        ) : FlowType

        object AddSecondaryCurrency : FlowType

        object EditSecondaryCurrency : FlowType
    }
}