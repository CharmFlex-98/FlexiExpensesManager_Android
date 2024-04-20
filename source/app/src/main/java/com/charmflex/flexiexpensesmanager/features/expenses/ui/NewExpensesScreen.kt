package com.charmflex.flexiexpensesmanager.features.expenses.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.SGTextField
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewExpensesScreen(
    viewModel: NewExpensesViewModel
) {
    val viewState by viewModel.viewState.collectAsState()

    SGScaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Create New Expenses",
                style = TextStyle(fontSize = 20.sp)
            )
        },
            navigationIcon = {
                IconButton(onClick = viewModel::onBack) {
                    SGIcons.ArrowBack()
                }
            })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(grid_x2)
        ) {
            viewState.fields.forEach {
                SGTextField(
                    modifier = Modifier
                        .padding(vertical = grid_x1)
                        .fillMaxWidth(),
                    label = stringResource(id = it.labelId),
                    value = it.value,
                    hint = stringResource(it.hintId)
                ) { newValue ->
                    viewModel.onFieldValueChanged(it, newValue)
                }
            }
        }
    }
}