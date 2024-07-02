package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.charmflex.flexiexpensesmanager.R

@Composable
internal fun SGLottieAnimation(
    modifier: Modifier,
    composition: LottieComposition? = null,
    isPlaying: Boolean = true,
    restartOnPlay: Boolean = true,
    reverseOnRepeat: Boolean = false,
    clipSpec: LottieClipSpec? = null,
    speed: Float = 1f,
    iterations: Int = 1
) {
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying,
        restartOnPlay,
        reverseOnRepeat,
        clipSpec,
        speed,
        iterations,
    )
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress })
}

@Composable
fun NoResultAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_result))
    SGLottieAnimation(
        modifier = modifier.size(grid_x14),
        composition = composition,
        iterations = 1
    )
}

@Composable
fun NoResultContent(
    description: String,
    fontSize: TextUnit = 12.sp
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoResultAnimation()
            Text(
                text = description,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = fontSize,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LoadingAnimationSurface(
    modifier: Modifier = Modifier,
) {
    Surface(color = Color.Black.copy(alpha = 0.7f), modifier = Modifier.fillMaxSize()) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_anim))
        SGLottieAnimation(
            modifier = modifier,
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}

@Composable
fun SuccessGreenTickAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_tick_anim))
    SGLottieAnimation(
        modifier = modifier.size(grid_x16),
        composition = composition,
        iterations = 1
    )
}

@Composable
fun WarningAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.warning_anim))
    SGLottieAnimation(
        modifier = modifier.size(grid_x30),
        composition = composition,
        iterations = 1
    )
}

@Composable
fun SetupAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.setup_anim))
    SGLottieAnimation(
        modifier = modifier.size(grid_x30),
        composition = composition,
        iterations = 1
    )
}


@Composable
fun Loader(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_dots_anim))
    SGLottieAnimation(
        modifier = modifier.size(grid_x10),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun Welcome(
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.welcome_anim))
    SGLottieAnimation(
        modifier = modifier.size(grid_x30),
        composition = composition,
        iterations = 1
    )
}

// General Animation
@Composable
fun SGAnimatedTransition(
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    isVisible: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

@Composable
fun BoxScope.CircularProgressIndicatorFullScreen() {
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = Modifier.fillMaxSize().clickable(interactionSource = interactionSource, indication = null, onClick = {}), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MoneyOutInAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.money_out_in))
    SGLottieAnimation(
        modifier = modifier.size(grid_x10),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}

@Composable
fun Money3DAnimation(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.money_3d))
    SGLottieAnimation(
        modifier = modifier.size(grid_x10),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}