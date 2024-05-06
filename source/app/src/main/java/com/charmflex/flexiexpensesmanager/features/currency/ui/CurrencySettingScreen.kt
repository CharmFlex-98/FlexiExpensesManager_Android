package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading2
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun CurrencySettingScreen(
    viewModel: CurrencySettingViewModel
) {
    val viewState by viewModel.viewState.collectAsState()

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2)
    ) {
        Column {
            FeColumnContainer(
                modifier = Modifier.padding(vertical = grid_x1)
            ) {
                FEHeading2(text = "Main Currency")
                SGTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Set main currency",
                    value = viewState.mainCurrency
                ) {
                    viewModel.onMainCurrencySelected(it)
                }
            }
            FeColumnContainer(
                modifier = Modifier.padding(vertical = grid_x1)
            ) {
                FEHeading2(text = "Secondary Currency")
                SGTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Set secondary currency",
                    value = viewState.secondaryCurrency
                ) {
                    viewModel.onSecondaryCurrencySelected(it)
                }
            }
        }

    }
}