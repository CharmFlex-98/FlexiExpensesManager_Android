package com.charmflex.flexiexpensesmanager.core.navigation.routes

object AccountRoutes {

    private const val ROOT = "account"

    object Args {
        const val IMPORT_FIX_ACCOUNT_NAME = "import_fix_account_name"
        const val ACCOUNT_ID = "account_id"
        const val ACCOUNT_DETAIL_DATE_FILTER = "account_detail_date_filter"
    }

    val EDITOR = buildRoute("$ROOT/editor") {
        addArg(Args.IMPORT_FIX_ACCOUNT_NAME)
    }

    val DETAIL = buildRoute("$ROOT/detail") {
        addArg(Args.ACCOUNT_ID)
    }

    fun editorDestination(importFixAccountName: String? = null): String = buildDestination(EDITOR) {
        importFixAccountName?.let { withArg(Args.IMPORT_FIX_ACCOUNT_NAME, it) }
    }

    fun accountDetailDestination(accountId: Int): String = buildDestination(DETAIL) {
        withArg(Args.ACCOUNT_ID, accountId.toString())
    }

}