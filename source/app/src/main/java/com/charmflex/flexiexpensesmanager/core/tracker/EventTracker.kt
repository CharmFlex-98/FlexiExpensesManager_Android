package com.charmflex.flexiexpensesmanager.core.tracker

internal interface EventTracker {
    fun track(eventData: EventData)
    fun registerUser(userData: UserData)
}

internal interface EventData {
    val eventName: String
    val props: Map<String, String>?

    companion object {
        fun simpleEvent(eventName: String): EventData {
            return event(eventName, null)
        }

        fun event(eventName: String, props: Map<String, String>?): EventData {
            return object : EventData {
                override val eventName: String
                    get() = eventName
                override val props: Map<String, String>?
                    get() = props
            }
        }
    }
}

internal data class UserData(
    val id: String,
    val name: String,
    val email: String
)

