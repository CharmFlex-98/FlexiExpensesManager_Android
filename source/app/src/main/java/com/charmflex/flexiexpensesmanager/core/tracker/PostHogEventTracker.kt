package com.charmflex.flexiexpensesmanager.core.tracker

import android.content.Context
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import com.posthog.android.replay.PostHogSessionReplayConfig
import javax.inject.Inject

internal class PostHogEventTracker @Inject constructor(
    private val appContext: Context
) : EventTracker {
    companion object {
        const val POSTHOG_API_KEY = "phc_1yyz39JoHNqBXw0wRC9O4bswH8HU2JBOnZTttPRjXEs"
        const val POSTHOG_HOST = "https://us.i.posthog.com"
    }

    fun init() {
        val postHogConfig = PostHogAndroidConfig(
            apiKey = POSTHOG_API_KEY,
            host = POSTHOG_HOST,
            sessionReplayConfig = PostHogSessionReplayConfig(screenshot = true)
        )
        postHogConfig.sessionReplay = true
        PostHogAndroid.setup(appContext, postHogConfig)
    }

    override fun track(eventData: EventData) {
        PostHog.capture(eventData.eventName)
    }

    override fun registerUser(userData: UserData) {
        PostHog.identify(userData.id, mapOf("name" to userData.name, "email" to userData.email))
    }
}