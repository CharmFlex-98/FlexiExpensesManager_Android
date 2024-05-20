package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionTagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TagSettingViewModel @Inject constructor(
    private val transactionTagRepository: TransactionTagRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {

    private val _viewState = MutableStateFlow(TagSettingViewState())
    val viewState = _viewState.asStateFlow()

    fun onUpdateTagValue(tagName: String) {
        _viewState.update {
            it.copy(
                tagName = tagName
            )
        }
    }
    fun addNewTag() {
        viewModelScope.launch {
            resultOf {
                transactionTagRepository.addTag(_viewState.value.tagName)
            }.fold(
                onSuccess = {
                    routeNavigator.pop()
                },
                onFailure = {}
            )
        }
    }
}


internal data class TagSettingViewState(
    val tagName: String = ""
)