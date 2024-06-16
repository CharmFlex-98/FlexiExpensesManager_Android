package com.charmflex.flexiexpensesmanager.features.backup.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEBody3
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.FEMetaData1
import com.charmflex.flexiexpensesmanager.ui_common.ListTable
import com.charmflex.flexiexpensesmanager.ui_common.SGLargePrimaryButton
import com.charmflex.flexiexpensesmanager.ui_common.SGScaffold
import com.charmflex.flexiexpensesmanager.ui_common.TransparentBackground
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import kotlin.math.round

@Composable
internal fun ImportDataScreen(importDataViewModel: ImportDataViewModel) {

    val viewState by importDataViewModel.viewState.collectAsState()

    SGScaffold(
        modifier = Modifier.padding(grid_x2)
    ) {
        if (viewState.importedData.isEmpty()) {
            PreLoadScreen(viewState = viewState) {
                importDataViewModel.importData(it)
            }
        } else {
            LoadedScreen(
                viewState = viewState,
                onFixError = {
                    importDataViewModel.onFixError(it)
                },
            ) {
                importDataViewModel.saveData()
            }
        }
    }

    if (viewState.isLoading) {
        BackHandler {
            // Do nothing
        }

        TransparentBackground(0.4f) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { viewState.progress },
                    )
                    if (viewState.progress == 0f) FEBody3(
                        modifier = Modifier.padding(grid_x1),
                        text = "Initializing...",
                        color = Color.White
                    ) else FEBody3(
                        modifier = Modifier.padding(grid_x1),
                        text = "${round(viewState.progress * 100)}%", color = Color.White
                    )
                }

            }
        }
    }
}

@Composable
private fun ColumnScope.PreLoadScreen(
    viewState: ImportDataViewState,
    onImportClick: (uri: Uri) -> Unit
) {
    val excelPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { excelUri ->
        excelUri?.let {
            onImportClick(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        FEBody2(text = "Click to load data")
    }
    SGLargePrimaryButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(grid_x2), text = "Import"
    ) {
        excelPickerLauncher.launch(
            arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
        )
    }

}

@Composable
private fun ColumnScope.LoadedScreen(
    viewState: ImportDataViewState,
    onFixError: (ImportedData.MissingData) -> Unit,
    onSave: () -> Unit
) {
    val tabs = listOf("All Data", "Error")
    var tabIndex by remember {
        mutableStateOf(0)
    }
    TabRow(selectedTabIndex = tabIndex) {
        tabs.forEachIndexed { index, tabName ->
            Tab(modifier = Modifier.padding(grid_x2), selected = index == tabIndex,
                onClick = { tabIndex = index }
            ) {
                FEHeading4(text = tabName)
            }
        }
    }
    when (tabIndex) {
        0 -> {
            ListTable(
                modifier = Modifier.weight(1f),
                items = viewState.importedData
            ) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(grid_x1)
                ) {
                    Row {
                        Box(
                            modifier = Modifier.padding(grid_x1),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${index + 1})")
                        }
                        Column {
                            FEBody3(text = item.transactionName)
                            when (val it = item.categoryColumns) {
                                is ImportedData.RequiredDataState.Missing -> {
                                    MissingItemText(
                                        entityName = "category",
                                        entityItemName = it.name
                                    )
                                }

                                is ImportedData.RequiredDataState.Acquired -> {
                                    AcquiredItemText(
                                        entityName = "category",
                                        entityItemName = it.name
                                    )
                                }

                                else -> {}
                            }
                            item.accountFrom?.let {
                                when (it) {
                                    is ImportedData.RequiredDataState.Missing -> {
                                        MissingItemText(
                                            entityName = "account from",
                                            entityItemName = it.name
                                        )
                                    }

                                    is ImportedData.RequiredDataState.Acquired -> {
                                        AcquiredItemText(
                                            entityName = "account from",
                                            entityItemName = it.name
                                        )
                                    }
                                }
                            }

                            item.accountTo?.let {
                                when (it) {
                                    is ImportedData.RequiredDataState.Missing -> {
                                        MissingItemText(
                                            entityName = "account to",
                                            entityItemName = it.name
                                        )
                                    }

                                    is ImportedData.RequiredDataState.Acquired -> {
                                        AcquiredItemText(
                                            entityName = "account to",
                                            entityItemName = it.name
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            SGLargePrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = grid_x1),
                text = "Save"
            ) {
                onSave()
            }
        }

        1 -> {
            ListTable(items = viewState.missingData.toList()) { index, item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable {
                            onFixError(item)
                        }
                        .padding(grid_x2)
                ) {
                    when (item.dataType) {
                        ImportedData.MissingData.DataType.ACCOUNT -> {
                            FEBody2(text = "Create account: ${item.name}")
                        }

                        ImportedData.MissingData.DataType.EXPENSES_CATEGORY -> {
                            FEBody2(text = "Create expenses category: ${item.name}")
                        }

                        ImportedData.MissingData.DataType.INCOME_CATEGORY -> {
                            FEBody2(text = "Create income category: ${item.name}")
                        }

                        ImportedData.MissingData.DataType.TAG -> {
                            FEBody2(text = "Create tag: ${item.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MissingItemText(
    entityName: String,
    entityItemName: String
) {
    FEMetaData1(text = "$entityName: $entityItemName is missing!", color = Color.Red)
}

@Composable
private fun AcquiredItemText(
    entityName: String,
    entityItemName: String
) {
    FEMetaData1(text = "$entityName: $entityItemName")
}