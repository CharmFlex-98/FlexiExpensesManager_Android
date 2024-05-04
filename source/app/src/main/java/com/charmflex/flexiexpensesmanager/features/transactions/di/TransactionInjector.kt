package com.charmflex.flexiexpensesmanager.features.transactions.di

import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.NewTransactionViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail.TransactionDetailViewModel

internal interface TransactionInjector {
    fun newTransactionViewModel(): NewTransactionViewModel

    fun transactionDetailViewModelFactory(): TransactionDetailViewModel.Factory
}