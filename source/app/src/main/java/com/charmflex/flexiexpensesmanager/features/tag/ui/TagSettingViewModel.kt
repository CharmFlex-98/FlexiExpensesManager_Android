package com.charmflex.flexiexpensesmanager.features.tag.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.ResourcesProvider
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.tag.domain.model.Tag
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import com.charmflex.flexiexpensesmanager.ui_common.features.SETTING_EDITOR_TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TagSettingViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val routeNavigator: RouteNavigator,
    private val resourcesProvider: ResourcesProvider
) : ViewModel() {
    private var flowType: TagSettingFlow = TagSettingFlow.Default

    private val _viewState = MutableStateFlow(TagSettingViewState())
    val viewState = _viewState.asStateFlow()

    private val _snackBarState: MutableStateFlow<SnackBarState> = MutableStateFlow(SnackBarState.None)
    val snackBarState = _snackBarState.asStateFlow()

    private val _bsState = MutableStateFlow<TagSettingViewState.TagSettingDialogState?>(null)
    val bsState = _bsState.asStateFlow()

    init {
        observeTags()
    }

    fun initFlow(fixImportTagName: String?) {
        if (fixImportTagName != null) flowType = TagSettingFlow.ImportFix(fixImportTagName) else TagSettingFlow.Default
        when (val ft = flowType) {
            is TagSettingFlow.ImportFix -> {
                onToggleEditor(null)
                getTagField()?.let { f ->
                    onUpdateFields(feField = f, ft.tagName)
                }

            }
            else -> {}
        }
    }

    fun onUpdateFields(feField: FEField, newValue: String, newId: String? = null) {
        val tagEditorState = _viewState.value.tagEditorState
        tagEditorState?.let {
            val updatedFields = it.fields.map { item ->
                if (item == feField) return@map item.copy(valueItem = item.valueItem.copy(id = newId ?: "", value = newValue))
                item
            }
            _viewState.update { vs ->
                vs.copy(
                    tagEditorState = it.copy(fields = updatedFields)
                )
            }
        }
    }

    private fun observeTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collectLatest { res ->
                _viewState.update {
                    it.copy(
                        tags = res
                    )
                }
            }
        }
    }

    fun addNewTag() {
        val tagName = getTagField()?.valueItem?.value
        if (tagName.isNullOrEmpty()) {
            handleError(TagErrorState.BlankTag)
            return
        }

        viewModelScope.launch {
            resultOf {
                tagRepository.addTag(tagName)
            }.fold(
                onSuccess = {
                    onBack()
                },
                onFailure = {}
            )
        }
    }

    private fun getTagField(): FEField? {
        return _viewState.value.tagEditorState?.fields?.firstOrNull { it.id == SETTING_EDITOR_TAG }
    }

    fun onBack() {
        when (flowType) {
            is TagSettingFlow.Default -> {
                if (viewState.value.isEditorMode) _viewState.value = _viewState.value.copy(tagEditorState = null)
                else routeNavigator.pop()
            }
            is TagSettingFlow.ImportFix -> {
                routeNavigator.popWithArguments(
                    mapOf(
                        BackupRoutes.Args.UPDATE_IMPORT_DATA to true
                    )
                )
            }
        }
    }

    fun onDeleteIconTap(tag: Tag) {
        _viewState.update {
            it.copy(
                dialogState = TagSettingViewState.TagSettingDialogState.DeleteWarning(tag)
            )
        }
    }

    fun onConfirmDelete(tag: Tag) {
        viewModelScope.launch {
            tagRepository.deleteTag(tagId = tag.id)
            _viewState.update {
                it.copy(
                    dialogState = null
                )
            }
        }
    }

    fun onCloseDialog() {
        _viewState.update {
            it.copy(
                dialogState = null
            )
        }
    }

    fun onToggleEditor(selectedTag: Tag?) {
        _viewState.update {
            it.copy(
                tagEditorState = TagSettingViewState.TagEditorState(tagForEdit = selectedTag)
            )
        }
    }

    private fun handleError(errorState: TagErrorState) {
        _snackBarState.value = SnackBarState.Error(message = resourcesProvider.getString(errorState.errorId))
    }
}


internal data class TagSettingViewState(
    val selectedTag: Tag? = null,
    val tags: List<Tag> = emptyList(),
    val tagEditorState: TagEditorState? = null,
    val dialogState: TagSettingDialogState? = null
) {
    val isEditorMode: Boolean get() = tagEditorState != null

    internal data class TagEditorState(
        val fields: List<FEField> = tagFieldsProvider(),
        val tagForEdit: Tag?
    )

    internal sealed interface TagSettingDialogState {
        data object None: TagSettingDialogState
        data class DeleteWarning(
            val tag: Tag,
        ) : TagSettingDialogState
    }
}

private sealed class TagErrorState(
    val errorId: Int
) {
    data object BlankTag: TagErrorState(R.string.generic_blank_input_invalid)
}

internal sealed interface TagSettingFlow {
    object Default : TagSettingFlow
    data class ImportFix(
        val tagName: String
    ) : TagSettingFlow
}

private fun tagFieldsProvider(): List<FEField> {
    return listOf(
        FEField(
            id = SETTING_EDITOR_TAG,
            labelId = R.string.tag_setting_tag_name,
            hintId = R.string.tag_setting_tag_name_hint,
            type = FEField.FieldType.Text,
        )
    )
}