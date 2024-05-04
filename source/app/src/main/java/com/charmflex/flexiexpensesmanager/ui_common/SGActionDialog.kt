package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SGActionDialog(
    title: String,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    primaryButtonText: String,
    secondaryButtonText: String? = null,
    onConfirm: () -> Unit,
) {
    SGDialog(title = title, subtitle = text, icon = icon, onDismissRequest = onDismissRequest) {
        SGButtonGroupHorizontal {
            if (secondaryButtonText != null) {
                SGMediumSecondaryButton(
                    modifier = Modifier.weight(1f),
                    text = secondaryButtonText
                ) {
                    onDismissRequest()
                }
            }
            SGMediumPrimaryButton(modifier = Modifier.weight(1f), text = primaryButtonText) {
                onConfirm()
            }
        }
    }
}

@Composable
fun SGDialog(
    title: String,
    subtitle: String,
    icon: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .width(grid_x47)
                .wrapContentHeight(),
            shape = RoundedCornerShape(grid_x2),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(grid_x2)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }
                }
                Text(
                    modifier = Modifier.padding(vertical = grid_x1),
                    text = title,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        lineHeight = 28.sp
                    )
                )
                Text(
                    modifier = Modifier.padding(vertical = grid_x1),
                    text = subtitle,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(grid_x1))
                if (bottomContent != null) bottomContent()
            }
        }
    }
}

@Composable
fun SuccessStatusDialog(
    title: String,
    subtitle: String,
    bottomLayout: @Composable ColumnScope.() -> Unit
) {
    SGDialog(
        title = title,
        subtitle = subtitle,
        icon = { SuccessGreenTickAnimation() },
        onDismissRequest = { },
    ) {
        bottomLayout()
    }
}
