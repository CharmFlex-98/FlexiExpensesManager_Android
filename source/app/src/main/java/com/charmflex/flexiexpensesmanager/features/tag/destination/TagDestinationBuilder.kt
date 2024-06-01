package com.charmflex.flexiexpensesmanager.features.tag.destination

import androidx.navigation.NavArgs
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.core.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.navigation.routes.TagRoutes
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.tag.ui.TagSettingScreen

internal class TagDestinationBuilder : DestinationBuilder{
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }

    override fun NavGraphBuilder.buildGraph() {
        tagSetting()
    }

    private fun NavGraphBuilder.tagSetting() {
        composable(
            TagRoutes.ADD_NEW_TAG_ROUTE,
            arguments = listOf(
                navArgument(TagRoutes.Args.IMPORT_FIX_TAG_NAME) {
                    nullable = true
                    type = NavType.StringType
                }
            )
        ) {
            val importFixTagName = it.arguments?.getString(TagRoutes.Args.IMPORT_FIX_TAG_NAME)
            val viewModel = getViewModel {
                appComponent.tagSettingViewModel().apply {
                    initFlow(importFixTagName)
                }
            }
            TagSettingScreen(viewModel = viewModel)
        }
    }
}