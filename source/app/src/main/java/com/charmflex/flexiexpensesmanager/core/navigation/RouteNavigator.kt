package com.charmflex.flexiexpensesmanager.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.utils.navigateAndPopUpTo
import com.charmflex.flexiexpensesmanager.core.utils.navigateTo
import com.charmflex.flexiexpensesmanager.core.utils.popWithArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

interface RouteNavigator {

    val navigationEvent: Flow<NavigationEvent>

    fun navigateTo(route: String, arg: Map<String, Any>? = null)
    fun navigateAndPopUpTo(route: String, popUpToRouteInclusive: String)
    fun pop()
    fun popWithArguments(data: Map<String, Any>)

    companion object {
        val instance by lazy { RouteNavigatorImpl() }
    }
}

class RouteNavigatorImpl @Inject constructor() : RouteNavigator {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>(extraBufferCapacity = 1)
    override val navigationEvent: Flow<NavigationEvent>
        get() = _navigationEvent.asSharedFlow()

    override fun navigateTo(route: String, arg: Map<String, Any>?) {
        _navigationEvent.tryEmit(NavigateTo(route, arg))
    }

    override fun navigateAndPopUpTo(route: String, popUpToRouteInclusive: String) {
        _navigationEvent.tryEmit(NavigateAndPopUpTo(route = route, popToRouteInclusive = popUpToRouteInclusive))
    }

    override fun pop() {
        _navigationEvent.tryEmit(Pop)
    }

    override fun popWithArguments(data: Map<String, Any>) {
        _navigationEvent.tryEmit(PopWithArguments(data))
    }
}

sealed interface NavigationEvent

data class NavigateTo(
    val route: String,
    val args: Map<String, Any>?
) : NavigationEvent
data class NavigateAndPopUpTo(val route: String, val popToRouteInclusive: String): NavigationEvent
object Pop : NavigationEvent
data class PopWithArguments(
    val data: Map<String, Any>
) : NavigationEvent

@Composable
fun RouteNavigatorListener(
    routeNavigator: RouteNavigator,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        routeNavigator.navigationEvent.collect {
            when (it) {
                is NavigateTo -> navController.navigateTo(it.route, it.args)
                is NavigateAndPopUpTo -> navController.navigateAndPopUpTo(it.route, it.popToRouteInclusive)
                is Pop -> navController.popBackStack()
                is PopWithArguments -> navController.popWithArgs(it.data)
            }
        }
    }
}

internal fun RouteNavigator.popWithHomeRefresh() {
    this.popWithArguments(
        mapOf(
            HomeRoutes.Args.HOME_REFRESH to true
        )
    )
}