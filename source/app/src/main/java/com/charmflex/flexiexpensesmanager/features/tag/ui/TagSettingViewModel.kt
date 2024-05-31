package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TagSettingViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private lateinit var flowType: TagSettingFlow

    private val _viewState = MutableStateFlow(TagSettingViewState())
    val viewState = _viewState.asStateFlow()

    fun initFlow(fixImportTagName: String?) {
        if (fixImportTagName != null) flowType = TagSettingFlow.IMPORT_FIX else TagSettingFlow.DEFAULT
    }

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
                tagRepository.addTag(_viewState.value.tagName)
            }.fold(
                onSuccess = {
                    if (flowType == TagSettingFlow.DEFAULT) routeNavigator.pop()
                    else routeNavigator.popWithArguments(
                        mapOf(
                            BackupRoutes.Args.UPDATE_IMPORT_DATA to true
                        )
                    )
                },
                onFailure = {}
            )
        }
    }
}


internal data class TagSettingViewState(
    val tagName: String = ""
)

internal enum class TagSettingFlow {
    DEFAULT, IMPORT_FIX
}