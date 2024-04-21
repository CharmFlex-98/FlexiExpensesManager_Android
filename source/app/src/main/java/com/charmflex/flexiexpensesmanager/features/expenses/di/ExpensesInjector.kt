package com.charmflex.flexiexpensesmanager.features.expenses.di

import com.charmflex.flexiexpensesmanager.features.expenses.provider.ExpensesTypeProvider
import com.charmflex.flexiexpensesmanager.features.expenses.ui.NewExpensesViewModel

internal interface ExpensesInjector {
    fun newExpensesViewModel(): NewExpensesViewModel
}