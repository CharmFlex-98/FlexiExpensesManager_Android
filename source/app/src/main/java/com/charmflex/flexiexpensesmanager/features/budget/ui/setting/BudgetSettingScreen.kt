package com.charmflex.flexiexpensesmanager.features.budget.ui.setting

import androidx.activity.compose.BackHandler
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.CategorySelectionBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.BasicColumnContainerItemList
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGAnimatedTransition
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGModalBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.features.SettingEditorScreen
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.grid_x4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BudgetSettingScreen(
    budgetSettingViewModel: BudgetSettingViewModel
) {
    val viewState by budgetSettingViewModel.viewState.collectAsState()
    val bsState = viewState.bottomSheetState
    val bottomSheetState = rememberModalBottomSheetState()
    val currencyVisualTransformation = remember(viewState.currencyCode) {
        budgetSettingViewModel.currencyVisualTransformationBuilder().create(viewState.currencyCode)
    }

    BudgetListScreen(budgetSettingViewModel)

    SGAnimatedTransition(
        isVisible = viewState.isEditorOn,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        SettingEditorScreen(
            fields = viewState.fields,
            appBarTitle = stringResource(id = R.string.budget_setting_add_new_budget_app_title),
            onTextFieldChanged = { newValue, field ->
                budgetSettingViewModel.onFieldValueChanged(field, newValue)
            },
            onBack = { budgetSettingViewModel.onBack() },
            onCallBackFieldTap = {
                budgetSettingViewModel.onCallBackAction(it)
            },
            currencyVisualTransformation = currencyVisualTransformation,
            screenName = "BudgetSettingScreen"
        ) {
            budgetSettingViewModel.submitBudget()
        }
    }


    bsState?.let { state ->
        SGModalBottomSheet(
            modifier = Modifier.padding(grid_x2),
            sheetState = bottomSheetState,
            onDismiss = { budgetSettingViewModel.toggleBottomSheet(null) }
        ) {
            when (state) {
                is BudgetSettingViewState.BudgetSettingBottomSheetState.CategoryOption -> {
                    CategorySelectionBottomSheet(
                        onSelected = { id, name ->
                            budgetSettingViewModel.onFieldValueChanged(state.feField, name, id)
                            budgetSettingViewModel.toggleBottomSheet(null)
                        },
                        transactionCategories = viewState.expensesCategories
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetListScreen(
    viewModel: BudgetSettingViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        topBar = {
            BasicTopBar(
                title = stringResource(id = R.string.budget_setting_app_bar_title),
                actions = {
                    IconButton(onClick = { viewModel.toggleEditor(true) }) {
                        SGIcons.Add()
                    }
                }
            )
        }
    ) {
        BasicColumnContainerItemList(
            items = viewState.budgetListItem,
            itemContent = {
                FEBody2(modifier = Modifier.weight(1f), text = it.categoryName)
                FEBody2(text = it.budget, modifier = Modifier.padding(end = grid_x4))
            },
            actionItems = {
                IconButton(onClick = { viewModel.onDeleteBudget(it) }) {
                    SGIcons.Delete()
                }
            }
        )
    }
}