package com.charmflex.flexiexpensesmanager.features.home.di

import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.history.ExpensesHistoryViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_heat_map.ExpensesHeatMapViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.chart.expenses_pie_chart.ExpensesPieChartViewModel

internal interface HomeInjector {
    fun homeViewModel(): HomeViewModel
    fun dashBoardViewModel(): DashboardViewModel
    fun expensesPieChartViewModel(): ExpensesPieChartViewModel
    fun expensesHeatMapViewModel(): ExpensesHeatMapViewModel
    fun expensesHistoryViewModel(): ExpensesHistoryViewModel
    fun accountHomeViewModel(): AccountHomeViewModel
}