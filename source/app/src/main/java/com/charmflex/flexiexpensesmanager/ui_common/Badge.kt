package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun SGLabel(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = Color.Black,
    containerColor: Color,
    fontSize: TextUnit,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(grid_x0_5))
            .background(color = containerColor)
            .border(BorderStroke(grid_x0_25, Color.Black))

    ) {
        Text(
            modifier = modifier.padding(grid_x0_75),
            text = text,
            style = TextStyle(
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
@Preview
fun BadgePreview() {
    SGLabel(text = "In 2 days", fontSize = 11.sp, containerColor = Color.Red)
}