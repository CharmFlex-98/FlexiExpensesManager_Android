package com.charmflex.flexiexpensesmanager.ui_common

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun FEHeading1(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 40.sp,
            lineHeight = 44.sp
        )
    )
}

@Composable
fun FEHeading2(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp
        )
    )
}

@Composable
fun FEHeading3(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        )
    )
}

@Composable
fun FEHeading4(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp
        )
    )
}

@Composable
fun FEHeading5(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            color = color,
            fontSize = 18.sp,
            lineHeight = 24.sp
        )
    )
}

@Composable
fun FEBody1(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            lineHeight = 28.sp,
            color = color
        ),
    )
}

@Composable
fun FEBody2(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign? = null,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = color,
        ),
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun FEBody3(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            color = color,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    )
}

@Composable
fun FeCallout1(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            lineHeight = 24.sp
        )
    )
}

@Composable
fun FECallout2(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )
    )
}

@Composable
fun FECallout3(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = color
        )
    )
}

@Composable
fun FEMetaData1(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = color
        )
    )
}

@Composable
fun FEMetaData2(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified
) {
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = color
        )
    )
}

@Composable
fun <T> FEGeneralSelectionBottomSheet(
    title: String,
    items: List<T>,
    name: (T) -> String,
    onSelectItem: (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        FEHeading2(text = title)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
        ) {
            items.forEach {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectItem(it)
                        }
                        .padding(grid_x2),
                    contentAlignment = Alignment.Center
                ) {
                    FEBody2(text = name(it))
                }
            }
        }
    }
}