package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.features.home.usecases.GetExpensesPercentageUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.data.mapper.HeatMapLevelMapper
import com.google.j2objc.annotations.ReflectionSupport
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

internal class ExpensesHeatMapViewModel @Inject constructor(
    private val getExpensesPercentageUseCase: GetExpensesPercentageUseCase,
    private val heatMapLevelMapper: HeatMapLevelMapper
) : ViewModel() {
    var heatMapState = mutableStateOf<Map<LocalDate, Level>>(mapOf())
        private set

    init {
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            val res = getExpensesPercentageUseCase()
            heatMapState.value = heatMapLevelMapper.map(res)
        }
    }
}