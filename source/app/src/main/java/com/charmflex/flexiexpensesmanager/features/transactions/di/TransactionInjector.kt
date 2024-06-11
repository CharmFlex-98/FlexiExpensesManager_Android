package com.charmflex.flexiexpensesmanager.features.transactions.di

import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionEditorViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail.TransactionDetailViewModel

internal interface TransactionInjector {
    fun transactionEditorViewModelFactory(): TransactionEditorViewModel.Factory

    fun transactionDetailViewModelFactory(): TransactionDetailViewModel.Factory
}