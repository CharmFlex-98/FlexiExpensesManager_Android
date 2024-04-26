package com.charmflex.flexiexpensesmanager.features.expenses.di

import com.charmflex.flexiexpensesmanager.features.expenses.ui.new_expenses.NewExpensesViewModel

internal interface ExpensesInjector {
    fun newExpensesViewModel(): NewExpensesViewModel
}