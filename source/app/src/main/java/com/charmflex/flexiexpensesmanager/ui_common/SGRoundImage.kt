package com.charmflex.flexiexpensesmanager.ui_common

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.charmflex.flexiexpensesmanager.R

@Composable
fun SGRoundImage(
    modifier: Modifier = Modifier,
    @DrawableRes
    placeHolderIconId: Int? = null,
    @DrawableRes
    errorIconId: Int? = null,
    @DrawableRes
    fallbackIconId: Int = R.drawable.icon_people,
    source: Any?,
) {
    SGImage(modifier = modifier, placeHolderIconId = placeHolderIconId, errorIconId = errorIconId, fallbackIconId = fallbackIconId, source = source, shape = CircleShape)
}

@Composable
fun SGRoundCornerImage(
    modifier: Modifier = Modifier,
    @DrawableRes
    placeHolderIconId: Int? = null,
    @DrawableRes
    errorIconId: Int? = null,
    @DrawableRes
    fallbackIconId: Int = R.drawable.icon_people,
    source: Any?,
) {
    SGImage(modifier = modifier, source = source, placeHolderIconId = placeHolderIconId, errorIconId = errorIconId, fallbackIconId = fallbackIconId, shape = RoundedCornerShape(grid_x2))
}

@Composable
fun SGImage(
    modifier: Modifier = Modifier,
    source: Any?,
    @DrawableRes
    placeHolderIconId: Int? = null,
    @DrawableRes
    errorIconId: Int? = null,
    @DrawableRes
    fallbackIconId: Int = R.drawable.icon_people, // change later
    shape: Shape? = null
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(source)
            .apply {
                if (placeHolderIconId != null) placeholder(placeHolderIconId)
                if (errorIconId != null) error(errorIconId)
            }
            .fallback(fallbackIconId)
            .build(),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .run {
                if (shape != null) {
                    clip(shape)
                } else this
            },
        contentDescription = null
    )
}

@Composable
fun ImageOverlay(
    modifier: Modifier = Modifier,
    colorTop: Color = Color(0F, 0F, 0F, 0F),
    colorBottom: Color = Color(0F, 0F, 0F, 0.75F),
    alpha: Float = 0.5f,
    shape: Shape
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorTop, colorBottom)
                ),
                shape = shape
            )
            .alpha(alpha)
    ) {}
}

@Composable
fun InvalidOverlay(
    modifier: Modifier = Modifier,
    shape: Shape
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0F, 0F, 0F, 0.45F),
                shape = shape
            )
    ) {}
}

@Composable
@Preview
private fun RoundImagePreview() {
    SGRoundImage(source = "source/sdk/ui-common/src/main/assets/sample.jpeg")
}


@Composable
fun rememberImageLoaderState(): State<ImageLoaderState> {
    return remember { mutableStateOf<ImageLoaderState>(ImageLoaderState()) }
}

data class ImageLoaderState(
    val uri: Uri? = null
)