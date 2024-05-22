package com.charmflex.flexiexpensesmanager.features.tag.di

import com.charmflex.flexiexpensesmanager.features.tag.ui.TagSettingViewModel

internal interface TagInjector {
    fun tagSettingViewModel(): TagSettingViewModel
}