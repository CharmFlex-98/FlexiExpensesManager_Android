package com.charmflex.flexiexpensesmanager.core.navigation.routes

object TransactionRoute {
    const val ROOT = "TRANSACTION"
    const val NEW_TRANSACTION = "$ROOT/NEW_TRANSACTION"
    const val TRANSACTION_EDITOR = "$ROOT/TRANSACTION_EDITOR"
    const val TRANSACTION_DETAIL = "$ROOT/TRANSACTION_DETAIL"

    object Args {
        const val REFRESH_HOME = "$ROOT/Refresh"
        const val TRANSACTION_ID = "TRANSACTION_ID"
    }

    val transactionDetail = buildRoute(TRANSACTION_DETAIL) {
        addArg(Args.TRANSACTION_ID)
    }

    val newTransaction = buildRoute(NEW_TRANSACTION) {}

    val transactionEditor = buildRoute(TRANSACTION_EDITOR) {
        addArg(Args.TRANSACTION_ID)
    }

    fun newTransactionDestination(): String = buildDestination(NEW_TRANSACTION) {}

    fun editTransactionDestination(transactionId: Long): String = buildDestination(transactionEditor) {
        withArg(Args.TRANSACTION_ID, transactionId.toString())
    }

    fun transactionDetailDestination(transactionId: Long): String = buildDestination(transactionDetail) {
        withArg(Args.TRANSACTION_ID, transactionId.toString())
    }
}