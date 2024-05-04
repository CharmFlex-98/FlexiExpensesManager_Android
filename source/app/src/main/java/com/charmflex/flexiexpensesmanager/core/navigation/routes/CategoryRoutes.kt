package com.charmflex.flexiexpensesmanager.core.navigation.routes

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

internal object CategoryRoutes {
    private const val ROOT = "category"

    object Args {
        const val TRANSACTION_TYPE = "transaction_type"
    }

    val EDITOR = buildRoute("$ROOT/editor") {
        addArg(Args.TRANSACTION_TYPE)
    }

    fun editorDestination(transactionType: TransactionType): String {
        return buildDestination(EDITOR) {
            withArg(Args.TRANSACTION_TYPE, transactionType.name)
        }
    }
}