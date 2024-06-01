package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
        if (fixImportTagName != null) flowType = TagSettingFlow.ImportFix(fixImportTagName) else TagSettingFlow.Default
        when (val ft = flowType) {
            is TagSettingFlow.ImportFix -> {
                _viewState.update {
                    it.copy(
                        tagName = ft.tagName
                    )
                }
            }
            else -> {}
        }
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
                    if (flowType is TagSettingFlow.Default) routeNavigator.pop()
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

internal sealed interface TagSettingFlow {
    object Default : TagSettingFlow
    data class ImportFix(
        val tagName: String
    ) : TagSettingFlow
}