package com.charmflex.flexiexpensesmanager.core.navigation.routes

object AccountRoutes {

    private const val ROOT = "account"

    object Args {
        const val IMPORT_FIX_ACCOUNT_NAME = "import_fix_account_name"
    }

    val EDITOR = buildRoute("$ROOT/editor") {
        addArg(Args.IMPORT_FIX_ACCOUNT_NAME)
    }

    fun editorDestination(importFixAccountName: String? = null): String = buildDestination(EDITOR) {
        importFixAccountName?.let { withArg(Args.IMPORT_FIX_ACCOUNT_NAME, it) }
    }

}