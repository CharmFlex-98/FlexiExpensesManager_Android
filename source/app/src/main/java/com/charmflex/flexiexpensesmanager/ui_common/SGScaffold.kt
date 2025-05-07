package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.posthog.PostHog

@Composable
fun SGScaffold(
    modifier: Modifier = Modifier,
    screenName: String = "Undefined",
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    // When content is loading and event dispatching is not allowed
    isLoading: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    LaunchedEffect(Unit) {
        PostHog.screen(screenName)
    }
    Box {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar,
            bottomBar,
            snackbarHost,
            floatingActionButton,
            floatingActionButtonPosition,
            containerColor,
            contentColor,
            contentWindowInsets,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = modifier
                        .padding(it)
                        .fillMaxSize(),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment
                ) {
                    this.content()
                }
                if (isLoading) CircularProgressIndicatorFullScreen()
            }
        }
    }
}