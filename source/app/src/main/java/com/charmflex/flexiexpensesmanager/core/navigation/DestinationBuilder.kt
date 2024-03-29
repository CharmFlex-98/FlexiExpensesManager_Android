package com.charmflex.flexiexpensesmanager.core.navigation

import androidx.navigation.NavGraphBuilder

interface DestinationBuilder {
    fun NavGraphBuilder.buildGraph()
}