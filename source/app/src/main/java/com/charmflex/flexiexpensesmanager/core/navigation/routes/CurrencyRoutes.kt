package com.charmflex.flexiexpensesmanager.core.navigation.routes

object CurrencyRoutes {

    const val ROOT = "CURRENCY"
    const val USER_SECONDARY_CURRENCY = "USER_SECONDARY_CURRENCY"
    const val CURRENCY_SETTING = "$ROOT/CURRENCY_SETTING"

    object Args {
        const val CURRENCY_TYPE = "CURRENCY_TYPE"
        const val CURRENCY_TYPE_MAIN = "CURRENCY_TYPE_MAIN"
        const val CURRENCY_TYPE_SECONDARY = "CURRENCY_TYPE_SECONDARY"
        const val CURRENCY_TYPE_SECONDARY_EDIT = "CURRENCY_TYPE_SECONDARY_EDIT"
    }

    val currencySettingRoute = buildRoute(CURRENCY_SETTING) {
        addArg(Args.CURRENCY_TYPE)
    }

    fun currencySettingDestination(flowType: String) = buildDestination(currencySettingRoute) {
        withArg(Args.CURRENCY_TYPE, flowType)
    }
}