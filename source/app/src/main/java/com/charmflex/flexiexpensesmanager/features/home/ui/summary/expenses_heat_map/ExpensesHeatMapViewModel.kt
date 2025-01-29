package com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.mapper.TransactionHeatMapMapper
import com.charmflex.flexiexpensesmanager.features.home.usecases.GetExpensesDailyMedianRatioUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class ExpensesHeatMapViewModel @Inject constructor(
    private val getExpensesDailyMedianRatioUseCase: GetExpensesDailyMedianRatioUseCase,
    private val mapperFactory: TransactionHeatMapMapper.Factory
) : ViewModel() {
    private val lowerBoundary: Float = 0.5f
    private val higherBoundary: Float = 2f
    var heatMapState = mutableStateOf<Map<LocalDate, Color>>(mapOf())
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val mapper = mapperFactory.create(
                lowerBoundary,
                higherBoundary
            )
            getExpensesDailyMedianRatioUseCase().collectLatest {
                heatMapState.value = mapper.map(it)
            }
        }
    }
}