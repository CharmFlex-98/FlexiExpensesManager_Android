package com.charmflex.flexiexpensesmanager.core.navigation.routes

object BackupRoutes {

    const val ROOT = "backup"
    const val IMPORT_SETTING = "$ROOT/importSetting"

    object Args {
        const val UPDATE_IMPORT_DATA = "update_import_data"
    }

    val IMPORT_SETTING_ROUTE = buildRoute(IMPORT_SETTING) {
        addArg(Args.UPDATE_IMPORT_DATA)
    }

//    fun importSettingDestination(): String {
//        return buildDestination(IMPORT_SETTING_ROUTE) {
//            withArg()
//        }
//    }
}