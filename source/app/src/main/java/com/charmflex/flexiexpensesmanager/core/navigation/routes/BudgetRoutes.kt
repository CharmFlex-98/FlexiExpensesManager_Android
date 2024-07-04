package com.charmflex.flexiexpensesmanager.core.navigation.routes

internal object BudgetRoutes {

    private const val ROOT = "Budget"
    private const val SETTING = "$ROOT/SETTING"
    private const val BUDGET_DETAIL = "$ROOT/DETAIL"

    val budgetSettingRoute = buildRoute(SETTING) {}

    val budgetDetailRoute = buildRoute(BUDGET_DETAIL) {}
}