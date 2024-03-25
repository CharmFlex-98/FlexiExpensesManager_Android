package com.charmflex.flexiexpensesmanager.ui_common.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.charmflex.flexiexpensesmanager.ui_common.SGIcons
import com.charmflex.flexiexpensesmanager.ui_common.grid_x3
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonHighlightAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder

@Composable
fun SGToolTip(
    modifier: Modifier = Modifier,
    content: String
) {
    val bgColor = MaterialTheme.colorScheme.surfaceDim.toArgb()
    // create and remember a builder of Balloon.
    val builder = rememberBalloonBuilder {
        setArrowSize(20)
        setArrowPosition(0.5f)
        setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        setWidth(BalloonSizeSpec.WRAP)
        setHeight(BalloonSizeSpec.WRAP)
        setPadding(12)
        setMarginHorizontal(12)
        setCornerRadius(8f)
        setBalloonHighlightAnimation(BalloonHighlightAnimation.SHAKE)
        setBackgroundColor(bgColor)
        setBalloonAnimation(BalloonAnimation.FADE)
    }

    Balloon(
        builder = builder,
        balloonContent = {
            Text(text = content)
        }
    ) { balloonWindow ->
        IconButton(
            modifier = modifier,
            onClick = {
                balloonWindow.showAlignBottom() // display your balloon.
            }
        ) {
            SGIcons.Info(modifier = Modifier.size(grid_x3))
        }
    }
}