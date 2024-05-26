package com.charmflex.flexiexpensesmanager.features.tag.destination

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TagRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.tag.ui.TagSettingScreen

internal class TagDestinationBuilder : DestinationBuilder{
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }

    override fun NavGraphBuilder.buildGraph() {
        tagSetting()
    }

    private fun NavGraphBuilder.tagSetting() {
        composable(TagRoutes.ADD_NEW_TAG_ROUTE) {
            val viewModel = getViewModel {
                appComponent.tagSettingViewModel()
            }
            TagSettingScreen(viewModel = viewModel)
        }
    }
}