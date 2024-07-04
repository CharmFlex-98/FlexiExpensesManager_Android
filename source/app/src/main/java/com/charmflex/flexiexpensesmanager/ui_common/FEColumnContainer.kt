package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

@Composable
internal fun <T> BasicColumnContainerItemList(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemContent: @Composable RowScope.(T) -> Unit,
    actionItems: @Composable RowScope.(T) -> Unit
) {
    FeColumnContainer(
        modifier = modifier
    ) {
        items.forEachIndexed { index, it ->
            if (index != 0) HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = grid_x2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemContent(it)
                actionItems(it)
            }
        }
    }
}


@Composable
internal fun FeColumnContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(grid_x2))
            .background(backgroundColor)
            .padding(horizontal = grid_x2, vertical = grid_x1),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        content()
    }
}
