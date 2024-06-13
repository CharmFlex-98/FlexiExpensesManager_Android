package com.charmflex.flexiexpensesmanager.features.home.di

import com.charmflex.flexiexpensesmanager.features.home.ui.HomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.account.AccountHomeViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.dashboard.DashboardViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.history.TransactionHistoryViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.setting.SettingViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_heat_map.ExpensesHeatMapViewModel
import com.charmflex.flexiexpensesmanager.features.home.ui.summary.expenses_pie_chart.ExpensesPieChartViewModel

internal interface HomeInjector {
    fun homeViewModel(): HomeViewModel
    fun dashBoardViewModel(): DashboardViewModel
    fun expensesPieChartViewModel(): ExpensesPieChartViewModel
    fun expensesHeatMapViewModel(): ExpensesHeatMapViewModel
    fun expensesHistoryViewModelFactory(): TransactionHistoryViewModel.Factory
    fun accountHomeViewModel(): AccountHomeViewModel
    fun settingViewModel(): SettingViewModel
}