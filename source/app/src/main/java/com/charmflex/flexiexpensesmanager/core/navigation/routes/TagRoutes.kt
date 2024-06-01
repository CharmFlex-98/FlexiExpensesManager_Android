package com.charmflex.flexiexpensesmanager.core.navigation.routes

object TagRoutes {
    const val ROOT = "Tag"
    const val SETTING = "$ROOT/TagSetting"

    object Args {
        const val IMPORT_FIX = "is_import_fix"
        const val IMPORT_FIX_TAG_NAME = "import_fix_tag_name"
    }

    val ADD_NEW_TAG_ROUTE = buildRoute(SETTING){
        addArg(Args.IMPORT_FIX)
        addArg(Args.IMPORT_FIX_TAG_NAME)
    }

    fun addNewTagDestination(isImportFix: Boolean = false, newTagValue: String? = null) = buildDestination(ADD_NEW_TAG_ROUTE) {
        withArg(Args.IMPORT_FIX, isImportFix.toString())
        newTagValue?.let { withArg(Args.IMPORT_FIX_TAG_NAME, it) }
    }
}