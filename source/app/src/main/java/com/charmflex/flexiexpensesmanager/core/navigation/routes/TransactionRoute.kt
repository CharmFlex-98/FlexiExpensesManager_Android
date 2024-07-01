package com.charmflex.flexiexpensesmanager.core.navigation.routes

object TransactionRoute {
    const val ROOT = "TRANSACTION"
    const val NEW_TRANSACTION = "$ROOT/NEW_TRANSACTION"
    const val NEW_SCHEDULE_TRANSACTION = "$ROOT/NEW_SCHEDULE_TRANSFER"
    const val TRANSACTION_EDITOR = "$ROOT/TRANSACTION_EDITOR"
    const val SCHEDULED_TRANSACTION_EDITOR = "$ROOT/SCHEDULED_TRANSACTION_EDITOR"
    const val TRANSACTION_DETAIL = "$ROOT/TRANSACTION_DETAIL"

    object Args {
        const val REFRESH_HOME = "$ROOT/Refresh"
        const val TRANSACTION_ID = "TRANSACTION_ID"
        const val SCHEDULE_TRANSACTION_ID = "SCHEDULE_TRANSFER_ID"
    }

    val transactionDetail = buildRoute(TRANSACTION_DETAIL) {
        addArg(Args.TRANSACTION_ID)
    }

    val newTransaction = buildRoute(NEW_TRANSACTION) {}

    val newScheduledTransaction = buildRoute(NEW_SCHEDULE_TRANSACTION) {}

    val transactionEditor = buildRoute(TRANSACTION_EDITOR) {
        addArg(Args.TRANSACTION_ID)
    }
    val scheduledTransactionEditor = buildRoute(SCHEDULED_TRANSACTION_EDITOR) {
        addArg(Args.SCHEDULE_TRANSACTION_ID)
    }

    fun newTransactionDestination(): String = buildDestination(NEW_TRANSACTION) {}

    fun newScheduleTransactionDestination(): String = buildDestination(newScheduledTransaction) {}
    fun editTransactionDestination(transactionId: Long): String = buildDestination(transactionEditor) {
        withArg(Args.TRANSACTION_ID, transactionId.toString())
    }

    fun editScheduleTransactionDestination(scheduledTransactionId: Int): String = buildDestination(scheduledTransactionEditor) {
        withArg(Args.SCHEDULE_TRANSACTION_ID, scheduledTransactionId.toString())
    }

    fun transactionDetailDestination(transactionId: Long): String = buildDestination(transactionDetail) {
        withArg(Args.TRANSACTION_ID, transactionId.toString())
    }
}