package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGAutoCompleteTextField
import com.charmflex.flexiexpensesmanager.ui_common.SGModalBottomSheet
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.SearchBottomSheet
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

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2)
    ) {
        FeColumnContainer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = grid_x1),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FEHeading2(text = "Secondary Currency")
            SGTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Set secondary currency",
                readOnly = true,
                value = viewState.secondaryCurrency,
                onClicked = viewModel::onLaunchCurrencySelectionBottomSheet,
                onValueChange = {}
            )
            SGTextField(
                modifier = Modifier.fillMaxWidth(),
                label = "Currency rate",
                value = viewState.currencyRate
            ) {

            }
        }
    }

    if (bs.isVisible) SearchBottomSheet(
        sheetState = sheetState,
        onDismiss = { viewModel.onCloseCurrencySelectionBottomSheet() },
        searchFieldLabel = "Search currency",
        items = bs.items,
        onChanged = { viewModel.onSearchValueChanged(it) }
    ) { _, item ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.onSecondaryCurrencySelected(item) }) {
            Text(text = item)
        }
    }
}