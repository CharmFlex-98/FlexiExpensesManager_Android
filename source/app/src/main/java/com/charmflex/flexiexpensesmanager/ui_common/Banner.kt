package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// TODO: Evaluate before using this
@Composable
fun rememberTopBanner(
    title: String,
    subtitle: String,
    buttonText: String
) {
    var showBanner by rememberSaveable {
        mutableStateOf(true)
    }
    SGAnimatedTransition(
        enter = slideInVertically(initialOffsetY = { -it }) +
                expandVertically(expandFrom = Alignment.Top),
        exit = slideOutVertically(targetOffsetY = { -it }) +
                shrinkVertically(shrinkTowards = Alignment.Top),
        isVisible = showBanner,
    ) {
        SGBanner(title = title, subtitle = subtitle, primaryButtonText = buttonText) {
            showBanner = false
        }
    }
}

@Composable
private fun SGBanner(
    title: String,
    subtitle: String? = null,
    secondaryButtonText: String? = null,
    primaryButtonText: String,
    onSecondaryButtonClick: (() -> Unit)? = null,
    onPrimaryButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceDim)
            .padding(grid_x2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        subtitle?.let {
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = grid_x0_5),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
        Spacer(modifier = Modifier.padding(grid_x0_5))
        SGButtonGroupHorizontal {
            if (secondaryButtonText != null && onSecondaryButtonClick != null) SGSmallSecondaryButton(
                text = secondaryButtonText,
                onClick = onSecondaryButtonClick,
                modifier = Modifier.weight(1f)
            )
            SGSmallPrimaryButton(
                text = primaryButtonText,
                onClick = onPrimaryButtonClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}