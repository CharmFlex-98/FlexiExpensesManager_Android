package com.charmflex.flexiexpensesmanager.core.navigation.routes

import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

internal object CategoryRoutes {
    private const val ROOT = "category"

    object Args {
        const val TRANSACTION_TYPE = "transaction_type"
        const val IMPORT_FIX_CATEGORY_NAME = "import_fix_cat_name"
        const val CATEGORY_ID = "category_id"
        const val CATEGORY_NAME = "category_name"
        const val CATEGORY_DATE_FILTER = "CATEGORY_DATE_FILTER"
        const val CATEGORY_DATE_FILTER_2 = "CATEGORY_DATE_FILTER_2"
    }

    val EDITOR = buildRoute("$ROOT/editor") {
        addArg(Args.TRANSACTION_TYPE)
        addArg(Args.IMPORT_FIX_CATEGORY_NAME)
    }

    val STAT = buildRoute("$ROOT/stat") {}

    val CATEGORY_TRANSACTION_DETAIL = buildRoute("$ROOT/category_transaction_detail") {
        addArg(Args.CATEGORY_ID)
        addArg(Args.TRANSACTION_TYPE)
        addArg(Args.CATEGORY_NAME)
    }

    fun editorDestination(transactionType: TransactionType, newCategoryName: String? = null): String {
        return buildDestination(EDITOR) {
            withArg(Args.TRANSACTION_TYPE, transactionType.name)
            newCategoryName?.let { withArg(Args.IMPORT_FIX_CATEGORY_NAME, it) }
        }
    }

    fun categoryTransactionDetail(categoryId: Int, categoryName: String, transactionType: TransactionType): String {
        return buildDestination(CATEGORY_TRANSACTION_DETAIL) {
            withArg(Args.CATEGORY_ID, categoryId.toString())
            withArg(Args.TRANSACTION_TYPE, transactionType.name)
            withArg(Args.CATEGORY_NAME, categoryName)
        }
    }
}