package com.charmflex.flexiexpensesmanager.core.navigation.routes

import android.content.SharedPreferences.Editor
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType

object AccountRoutes {

    private const val ROOT = "account"

    val EDITOR = buildRoute("$ROOT/editor") {}

    fun editorDestination(): String = buildDestination(EDITOR) {}

}