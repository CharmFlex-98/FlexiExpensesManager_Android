package com.charmflex.flexiexpensesmanager.core.navigation.routes

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

internal object CategoryRoutes {
    private const val ROOT = "category"

    object Args {
        const val TRANSACTION_TYPE = "transaction_type"
        const val IMPORT_FIX = "is_import_fix"
        const val IMPORT_FIX_CATEGORY_NAME = "import_fix_cat_name"
    }

    val EDITOR = buildRoute("$ROOT/editor") {
        addArg(Args.TRANSACTION_TYPE)
        addArg(Args.IMPORT_FIX)
        addArg(Args.IMPORT_FIX_CATEGORY_NAME)
    }

    fun editorDestination(transactionType: TransactionType, isImportFix: Boolean = false, newCategoryName: String? = null): String {
        return buildDestination(EDITOR) {
            withArg(Args.TRANSACTION_TYPE, transactionType.name)
            withArg(Args.IMPORT_FIX, isImportFix.toString())
            newCategoryName?.let { withArg(Args.IMPORT_FIX_CATEGORY_NAME, it) }
        }
    }
}