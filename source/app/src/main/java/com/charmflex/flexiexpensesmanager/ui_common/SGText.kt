package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.charmflex.flexiexpensesmanager.ui_common.theme.FlexiExpensesManagerTheme

@Composable
fun SGBasicTwoLineItem(
    modifier: Modifier,
    title: String,
    trailingText: String? = null,
    content: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(grid_x1)
    ) {
        if (trailingText != null) {
            Row {
                Text(text = trailingText, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(text = title, color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
        }
        Text(text = content, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
    }
}

@Composable
fun SGBasicTwoLineIconsActionItem(
    modifier: Modifier = Modifier,
    iconSize: Dp,
    title: String,
    joinedCount: Int,
    maxAvailableCount: Int,
    icons: List<String?>,
    onClick: () -> Unit
) {
    val hiddenParticipantCount = joinedCount - icons.size
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(grid_x1)
    ) {
        Row {
            Text(modifier = Modifier.weight(1f), text = title, color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp, textAlign = TextAlign.Start)
            Text(text = "$joinedCount/$maxAvailableCount joined", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Row {
            for (i in icons) SGRoundImage(source = i, modifier = Modifier.size(iconSize))
            if (hiddenParticipantCount > 0) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    Text(text = "+$hiddenParticipantCount", color = MaterialTheme.colorScheme.primary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}


@Preview
@Composable
fun Preview1() {
    FlexiExpensesManagerTheme {
        Column {
            SGBasicTwoLineItem(modifier = Modifier.padding(horizontal = grid_x0_25), title = "Title", content = "My content")
            SGBasicTwoLineItem(modifier = Modifier.padding(horizontal = grid_x0_25), title = "Title", content = "My content")
        }
    }
}