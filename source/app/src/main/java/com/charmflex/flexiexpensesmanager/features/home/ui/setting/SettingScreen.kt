package com.charmflex.flexiexpensesmanager.features.home.ui.setting

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.charmflex.flexiexpensesmanager.ui_common.FEBody2
import com.charmflex.flexiexpensesmanager.ui_common.FEHeading4
import com.charmflex.flexiexpensesmanager.ui_common.FeColumnContainer
import com.charmflex.flexiexpensesmanager.ui_common.SGSnackBar
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarType
import com.charmflex.flexiexpensesmanager.ui_common.grid_x1
import com.charmflex.flexiexpensesmanager.ui_common.grid_x2
import com.charmflex.flexiexpensesmanager.ui_common.showSnackBarImmediately

@Composable
internal fun SettingScreen(viewModel: SettingViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    val snackbarState by viewModel.snackBarState
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarType = remember(snackbarState) {
        when (snackbarState) {
            is SnackBarState.Success -> SnackBarType.Success
            is SnackBarState.Error -> SnackBarType.Error
            else -> null
        }
    }

    LaunchedEffect(key1 = snackbarState) {
        when (val state = snackbarState) {
            is SnackBarState.Error -> {
                snackbarHostState.showSnackBarImmediately(state.message ?: "Something went wrong")
            }

            is SnackBarState.Success -> {
                snackbarHostState.showSnackBarImmediately(state.message)
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(grid_x2),
    ) {
        viewState.items.forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider(color = Color.White, thickness = 0.5.dp)
            Box(
                modifier = Modifier
                    .padding(vertical = grid_x1)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(grid_x2))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable {
                        viewModel.onTap(it.action)
                    }
                    .padding(grid_x2)
            ) {
                FEHeading4(text = it.title, color = MaterialTheme.colorScheme.onTertiary)
            }
        }
    }

    if (viewState.isLoading) Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }

    snackbarType?.let {
        SGSnackBar(snackBarHostState = snackbarHostState, snackBarType = it)
    }
}