package com.charmflex.flexiexpensesmanager.ui_common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp

@Composable
fun SGBottomNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    items: List<SGBottomNavItem>,
    isSelected: (String) -> Boolean,
    onClick: (SGBottomNavItem) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = isSelected(item.route),
                onClick = {
                    onClick(item)
                },
                label = { Text(text = stringResource(id = item.titleId))},
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = "")
                }
            )
        }
    }
}

data class SGBottomNavItem(
    val index: Int,
    @StringRes
    val titleId: Int,
    @DrawableRes
    val iconId: Int,
    val route: String
)

