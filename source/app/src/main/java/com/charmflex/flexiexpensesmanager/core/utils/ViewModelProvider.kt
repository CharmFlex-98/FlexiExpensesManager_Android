package com.charmflex.flexiexpensesmanager.core.utils

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner

@Composable
inline fun <reified T: ViewModel> getViewModel(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    savedStateRegistryOwner: SavedStateRegistryOwner =
        checkNotNull(LocalSavedStateRegistryOwner.current) {
            "No SavedStateRegistryOwner was provided via LocalSavedStateRegistryOwner"
        },
    defaultArgs: Bundle? = null,
    crossinline factoryProvider: (SavedStateHandle) -> T
): T {
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = object : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return factoryProvider(handle) as T
            }

        }
    )
}