package com.charmflex.flexiexpensesmanager.features.transactions.di

import com.charmflex.flexiexpensesmanager.features.category.category.ui.CategoryEditorViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_expenses.NewTransactionViewModel

internal interface TransactionInjector {
    fun newTransactionViewModel(): NewTransactionViewModel
}