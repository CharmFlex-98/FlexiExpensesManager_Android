package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData1
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SearchBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SearchItem
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
internal fun CurrencySettingScreen(
    viewModel: CurrencySettingViewModel
) {
    val viewState by viewModel.viewState.collectAsState()
    val bs = viewState.bottomSheetState
    val sheetState = rememberModalBottomSheetState()
    val isPrimaryCurrencySetting =
        viewModel.flowType is CurrencySettingViewState.FlowType.PrimaryCurrencySetting
    val title = if (isPrimaryCurrencySetting) "Primary Currency" else "Secondary Currency"
    val label = if (isPrimaryCurrencySetting) "Set primary currency" else "Set secondary currency"
    val actionButtonText = if (isPrimaryCurrencySetting) "Set" else "Add"

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        isLoading = viewState.isLoading,
        screenName = "CurrencySettingScreen"
    ) {
        FeColumnContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = grid_x1),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FEHeading2(text = title)
            SGTextField(
                modifier = Modifier.fillMaxWidth(),
                label = label,
                readOnly = true,
                value = viewState.currencyName,
                onClicked = viewModel::onLaunchCurrencySelectionBottomSheet,
                onValueChange = {}
            )
            if (viewState.isLoading.not() && viewModel.isMainCurrencyType().not()) {
                SGTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Currency rate",
                    value = viewState.currencyRate,
                    trailingIcon = {
                        if (viewState.currencyRate.isEmpty().not()) {
                            CurrencyTrailingIcon(isCustom = viewState.isCustom) {
                                viewModel.toggleCustomCurrency()
                            }
                        }
                    }
                ) {
                    viewModel.onCurrencyRateChanged(it)
                }
            }
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            SGLargePrimaryButton(modifier = Modifier.fillMaxWidth(), text = actionButtonText) {
                viewModel.addCurrency()
            }
        }
    }

    if (bs.isVisible) SearchBottomSheet(
        sheetState = sheetState,
        onDismiss = { viewModel.onCloseCurrencySelectionBottomSheet() },
        searchFieldLabel = "Search currency",
        items = bs.items.map { object : SearchItem {
            override val key: String
                get() = it
        } },
    ) { _, item ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.onCurrencySelected(item.key) }) {
            Text(text = item.key)
        }
    }
}

@Composable
private fun CurrencyTrailingIcon(
    isCustom: Boolean,
    onToggle: () -> Unit
) {
    IconButton(modifier = Modifier.padding(grid_x1), onClick = onToggle) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FEMetaData1(text = if (isCustom) "Custom" else "Latest")
            if (!isCustom) SGIcons.Tick(modifier = Modifier.background(color = Color.Green))
        }
    }
}