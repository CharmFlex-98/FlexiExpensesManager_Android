package com.charmflex.flexiexpensesmanager.core.navigation.routes

interface RouteBuilder {
    fun addArg(arg: String)
    fun build(): String
}

internal class RouteBuilderImpl(
    private val baseRoute: String
) : RouteBuilder {
    private val args = mutableListOf<String>()
    override fun addArg(arg: String) {
        args.add(arg)
    }

    override fun build(): String {
        return baseRoute + args.joinToString(prefix = "?", separator = "&") {
            "$it={$it}"
        }
    }
}

interface DestinationRouteBuilder {
    fun withArg(arg: String, value: String)
    fun withArgNullable(arg: String, value: String?)
    fun build(): String
}
internal class DestinationRouteBuilderImpl(
    private var route: String
) : DestinationRouteBuilder {
    override fun withArg(arg: String, value: String) {
        route = route.replace("{$arg}", value)
    }

    override fun withArgNullable(arg: String, value: String?) {
        value?.let { withArg(arg, it) }
    }

    override fun build(): String {
        return route
    }
}

fun buildRoute(baseRoute: String, builder: RouteBuilder.() -> Unit): String {
    return RouteBuilderImpl(baseRoute).apply(builder).build()
}

fun buildDestination(route: String, builder: DestinationRouteBuilder.() -> Unit): String {
    return DestinationRouteBuilderImpl(route).apply(builder).build()
}