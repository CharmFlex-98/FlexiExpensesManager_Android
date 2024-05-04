package com.charmflex.flexiexpensesmanager.core.navigation.routes

object TransactionRoute {
    const val ROOT = "TRANSACTION"
    const val NEW_RECORD = "$ROOT/NEW_RECORD"
    const val TRANSACTION_DETAIL = "$ROOT/TRANSACTION_DETAIL"

    object Args {
        const val REFRESH_HOME = "$ROOT/Refresh"
        const val TRANSACTION_ID = "TRANSACTION_ID"
    }

    val transactionDetail = buildRoute(TRANSACTION_DETAIL) {
        addArg(Args.TRANSACTION_ID)
    }

    fun transactionDetailDestination(transactionId: Long): String = buildDestination(transactionDetail) {
        withArg(Args.TRANSACTION_ID, transactionId.toString())
    }
}