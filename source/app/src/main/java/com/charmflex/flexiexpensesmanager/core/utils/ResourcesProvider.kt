package com.charmflex.flexiexpensesmanager.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import javax.inject.Inject

internal class ResourcesProvider @Inject constructor(
    val appContext: Context
) {
    fun getString(resId: Int): String {
        return appContext.resources.getString(resId)
    }

    fun getDrawable(drawableId: Int): Drawable {
        return appContext.resources.getDrawable(drawableId)
    }
}