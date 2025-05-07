package com.charmflex.flexiexpensesmanager.features.currency.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.charmflex.flexiexpensesmanager.ui_common.BasicTopBar
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@Composable
internal fun UserCurrencyScreen(
    viewModel: UserCurrencyViewModel
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.initialise()
    }

    SGScaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(grid_x2),
        screenName = "UserCurrencyScreen",
        horizontalAlignment = Alignment.CenterHorizontally,
        topBar = {
            BasicTopBar(
                "Currency",
                actions = {
                    IconButton(onClick = viewModel::onAddButtonTap) {
                        SGIcons.Add()
                    }
                }
            )
        }
    ) {
        if (viewState.currencyList.isNotEmpty()) {
            FeColumnContainer(
                modifier = Modifier.padding(vertical = grid_x2)
            ) {
                viewState.currencyList.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(grid_x1)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = it.name,
                            textAlign = TextAlign.Start
                        )
                        Text(text = it.rate.toString(), textAlign = TextAlign.End)
                    }
                }

            }
        }
    }
}