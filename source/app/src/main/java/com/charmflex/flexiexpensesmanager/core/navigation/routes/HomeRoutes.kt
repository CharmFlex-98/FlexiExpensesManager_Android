package com.charmflex.flexiexpensesmanager.core.navigation.routes

object HomeRoutes {
    const val ROOT = "ROOT"
    const val HOME = "HOME"
    const val SUMMARY = "$ROOT/SUMMARY"
    const val DETAIL = "$ROOT/DETAIL"
    const val ACCOUNTS = "$ROOT/ACCOUNTS"
    const val SETTING = "$ROOT/SETTING"


    object Args {
        const val HOME_REFRESH = "$HOME/refresh"
    }
}