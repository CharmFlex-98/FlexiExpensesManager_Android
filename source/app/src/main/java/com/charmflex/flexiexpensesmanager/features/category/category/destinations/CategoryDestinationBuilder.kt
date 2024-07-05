package com.charmflex.flexiexpensesmanager.features.category.category.destinations

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.charmflex.flexiexpensesmanager.app.di.AppComponentProvider
import com.charmflex.flexiexpensesmanager.core.navigation.DestinationBuilder
import com.charmflex.flexiexpensesmanager.core.navigation.FEHorizontalEnterFromEnd
import com.charmflex.flexiexpensesmanager.core.navigation.FEHorizontalEnterFromStart
import com.charmflex.flexiexpensesmanager.core.navigation.FEHorizontalExitToEnd
import com.charmflex.flexiexpensesmanager.core.navigation.FEVerticalSlideDown
import com.charmflex.flexiexpensesmanager.core.navigation.FEVerticalSlideUp
import com.charmflex.flexiexpensesmanager.core.navigation.routes.CategoryRoutes
import com.charmflex.flexiexpensesmanager.core.utils.DateFilter
import com.charmflex.flexiexpensesmanager.core.utils.getViewModel
import com.charmflex.flexiexpensesmanager.features.category.category.ui.CategoryEditorScreen
import com.charmflex.flexiexpensesmanager.features.category.category.ui.detail.CategoryDetailScreen
import com.charmflex.flexiexpensesmanager.features.category.category.ui.stat.CategoryStatScreen

internal class CategoryDestinationBuilder(
    private val navController: NavController
) : DestinationBuilder {
    private val appComponent by lazy { AppComponentProvider.instance.getAppComponent() }

    override fun NavGraphBuilder.buildGraph() {
        editorScreen()
        categoryStatScreen()
        categoryTransactionDetailScreen()
    }

    private fun NavGraphBuilder.editorScreen() {
        composable(
            CategoryRoutes.EDITOR,
            enterTransition = FEVerticalSlideUp,
            exitTransition = FEVerticalSlideDown
        ) {
            val importFixCatName =
                it.arguments?.getString(CategoryRoutes.Args.IMPORT_FIX_CATEGORY_NAME)
            val type = it.arguments?.getString(CategoryRoutes.Args.TRANSACTION_TYPE).orEmpty()
            val viewModel = getViewModel {
                appComponent.categoryEditorViewModel()
                    .apply { setType(type = type, importFixCatName) }
            }

            CategoryEditorScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.categoryStatScreen() {
        composable(
            CategoryRoutes.STAT,
            enterTransition = FEHorizontalEnterFromEnd,
            popExitTransition = FEHorizontalExitToEnd,
            popEnterTransition = FEHorizontalEnterFromStart,
        ) {
            val dateFilter = remember {
                navController.previousBackStackEntry?.savedStateHandle?.remove<DateFilter>(
                    CategoryRoutes.Args.CATEGORY_DATE_FILTER
                )
            }

            val viewModel = getViewModel {
                appComponent.categoryStatViewModel()
                    .apply { dateFilter?.let { onDateFilterChanged(dateFilter) } }
            }
            CategoryStatScreen(viewModel = viewModel)
        }
    }

    private fun NavGraphBuilder.categoryTransactionDetailScreen() {
        composable(
            CategoryRoutes.CATEGORY_TRANSACTION_DETAIL,
            enterTransition = FEHorizontalEnterFromEnd,
            popExitTransition = FEHorizontalExitToEnd,
            popEnterTransition = FEHorizontalEnterFromStart,
            arguments = listOf(
                navArgument(CategoryRoutes.Args.CATEGORY_ID) {
                    nullable = false
                    type = NavType.IntType
                },
                navArgument(CategoryRoutes.Args.TRANSACTION_TYPE) {
                    nullable = false
                    type = NavType.StringType
                },
                navArgument(CategoryRoutes.Args.CATEGORY_NAME) {
                    nullable = false
                    type = NavType.StringType
                }
            )
        ) {
            val dateFilter = remember {
                navController.previousBackStackEntry?.savedStateHandle?.remove<DateFilter>(
                    CategoryRoutes.Args.CATEGORY_DATE_FILTER
                )
            }

            val categoryId = it.arguments?.getInt(CategoryRoutes.Args.CATEGORY_ID) ?: -1
            val categoryName = it.arguments?.getString(CategoryRoutes.Args.CATEGORY_NAME) ?: ""
            val transactionType =
                it.arguments?.getString(CategoryRoutes.Args.TRANSACTION_TYPE) ?: ""
            val viewModel = getViewModel {
                appComponent.categoryDetailViewModelFactory()
                    .create(categoryId, categoryName, transactionType, dateFilter)
            }
            CategoryDetailScreen(viewModel = viewModel)
        }
    }

}