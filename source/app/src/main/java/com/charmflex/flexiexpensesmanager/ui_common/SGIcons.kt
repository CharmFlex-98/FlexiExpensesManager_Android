package com.charmflex.flexiexpensesmanager.ui_common

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.charmflex.flexiexpensesmanager.R

object SGIcons {

    @Composable
    fun Delete(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_delete), contentDescription = "")
    }

    @Composable
    fun Add(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_add), contentDescription = "")
    }

    @Composable
    fun Info(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_info), contentDescription = null)
    }

    @Composable
    fun ArrowBack(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
    }

    @Composable
    fun Calendar(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_calendar), contentDescription = null)
    }

    @Composable
    fun EmptyContent(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_empty_content), contentDescription = null)
    }

    @Composable
    fun Tick(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_correct), contentDescription = "")
    }

    @Composable
    fun Destination(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.icon_destination), contentDescription = null)
    }

    @Composable
    fun People(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.icon_people), contentDescription = null)
    }

    @Composable
    fun RightArrowThin(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_arrow_right_thin), contentDescription = null)
    }

    @Composable
    fun NextArrow(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_arrow_next), contentDescription = null)
    }

    @Composable
    fun EditIcon(
        modifier: Modifier = Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_edit), contentDescription = null)
    }

    @Composable
    fun LogoutIcon(
        modifier: Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_logout), contentDescription = null)
    }

    @Composable
    fun FilterIcon(
        modifier: Modifier
    ) {
        Icon(modifier = modifier, painter = painterResource(id = R.drawable.ic_filter), contentDescription = null)
    }
}
