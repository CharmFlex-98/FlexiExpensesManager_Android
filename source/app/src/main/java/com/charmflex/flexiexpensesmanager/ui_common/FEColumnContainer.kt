package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color


@Composable
internal fun FeColumnContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(vertical = grid_x2)
            .clip(RoundedCornerShape(grid_x2))
            .background(backgroundColor)
            .padding(grid_x2),
    ) {
        content()
    }
}
