package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun SGSnackBar(
    snackBarHostState: SnackbarHostState,
    snackBarType: SnackBarType
) {
    SnackbarHost(hostState = snackBarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = SnackBarType.containerColor(snackBarType = snackBarType),
            contentColor = SnackBarType.textColor(snackBarType = snackBarType)
        )
    }
}

sealed interface SnackBarType {
    object Success : SnackBarType
    object Error : SnackBarType

    companion object {
        @Composable
        fun containerColor(snackBarType: SnackBarType): Color {
            return when (snackBarType) {
                Success -> greenColor
                Error -> redColor
            }
        }

        fun textColor(snackBarType: SnackBarType): Color {
            return when (snackBarType) {
                Success -> Color.Black
                Error -> Color.White
            }
        }
    }
}

suspend fun SnackbarHostState.showSnackBarImmediately(message: String) {
    currentSnackbarData?.dismiss()
    showSnackbar(message = message, duration = SnackbarDuration.Short)
    delay(500)
}