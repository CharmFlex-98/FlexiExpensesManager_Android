package com.charmflex.flexiexpensesmanager.features.home.ui.dashboard

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal interface DashboardPlugin {
    @Composable
    fun ColumnScope.Render()
    fun refresh()
}