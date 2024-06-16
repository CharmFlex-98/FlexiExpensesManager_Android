package com.charmflex.flexiexpensesmanager.features.category.category.di

import com.charmflex.flexiexpensesmanager.features.category.category.ui.CategoryEditorViewModel
import com.charmflex.flexiexpensesmanager.features.category.category.ui.detail.CategoryStatViewModel

internal interface CategoryInjector {
    fun categoryEditorViewModel(): CategoryEditorViewModel

    fun categoryStatViewModel(): CategoryStatViewModel

}