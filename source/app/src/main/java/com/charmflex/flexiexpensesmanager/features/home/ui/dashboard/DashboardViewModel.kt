package com.charmflex.flexiexpensesmanager.features.home.ui.dashboard

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class DashboardViewModel @Inject constructor() {
    private val _plugins = MutableStateFlow<List<DashboardPlugin>>(emptyList())

    fun initPlugins(plugins: List<DashboardPlugin>) {
        _plugins.update { plugins }
    }

    fun refresh() {
        _plugins.value.forEach {
            it.refresh()
        }
    }
}