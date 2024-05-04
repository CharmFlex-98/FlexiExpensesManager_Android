package com.charmflex.flexiexpensesmanager.features.category.category.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.account.ui.AccountEditorViewState
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CategoryEditorViewModel @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private var editorTypeCode: TransactionType = TransactionType.EXPENSES

    private val _snackBarState: MutableStateFlow<SnackBarState> =
        MutableStateFlow(SnackBarState.None)
    val snackBarState = _snackBarState.asStateFlow()

    private val _viewState = MutableStateFlow(CategoryEditorViewState())
    val viewState = _viewState.asStateFlow()

    fun setType(type: String) {
        editorTypeCode = when {
            type == TransactionType.INCOME.name -> TransactionType.INCOME
            type == TransactionType.EXPENSES.name -> TransactionType.EXPENSES
            else -> TransactionType.EXPENSES
        }
        listenCategoryList()
    }

    private fun listenCategoryList() {
        viewModelScope.launch {
            categoryRepository.getAllCategories(editorTypeCode.name).collectLatest {
                toggleLoading(true)
                _viewState.update { state ->
                    state.copy(
                        categoryTree = it,
                        currentNode = state.currentNode?.let { currentNode ->
                            for (item in it.items) {
                                val res = getNode(currentNode.categoryId, item)
                                if (res != null) return@let res
                            }
                            return@let null
                        }
                    )
                }
                toggleLoading(false)
            }
        }
    }

    fun back() {
        if (_viewState.value.editorState.isOpened) {
            closeEditor()
            return
        }

        val currentNode = _viewState.value.currentNode
        if (currentNode == null) routeNavigator.pop()
        else {
            val parentNode = currentNode.parentNodeId?.let { parentCategoryId ->
                for (item in _viewState.value.categoryTree.items) {
                    val res = getNode(parentCategoryId, item)
                    if (res != null) return@let res
                }
                return@let null
            }

            _viewState.update {
                it.copy(
                    currentNode = parentNode
                )
            }
        }
    }

    private fun getNode(
        categoryId: Int,
        currentNode: TransactionCategories.Node
    ): TransactionCategories.Node? {
        if (currentNode.categoryId == categoryId) return currentNode
        else {
            for (item in currentNode.childNodes) {
                val res = getNode(categoryId, item)
                if (res != null) return res
            }

            return null
        }
    }

    fun launchDeleteDialog(id: Int) {
        _viewState.update {
            it.copy(
                dialogState = CategoryEditorViewState.DeleteDialogState(
                    categoryId = id
                )
            )
        }
    }

    fun closeDeleteDialog() {
        _viewState.update {
            it.copy(
                dialogState = null
            )
        }
    }

    fun deleteCategory() {
        val id = _viewState.value.dialogState?.categoryId ?: return
        resultOf {
            viewModelScope.launch {
                categoryRepository.deleteCategory(id)
            }
        }.fold(
            onSuccess = {
                _snackBarState.update { SnackBarState.Success("Delete success") }
            },
            onFailure = {
                _snackBarState.update { SnackBarState.Success("Something went wrong") }
            }
        )
    }

    fun onClickItem(node: TransactionCategories.Node) {
        _viewState.update {
            it.copy(
                currentNode = node
            )
        }
    }

    fun openEditor() {
        _viewState.update {
            it.copy(
                editorState = it.editorState.copy(
                    isOpened = true
                )
            )
        }
    }

    fun addNewCategory() {
        toggleLoading(true)
        val category = _viewState.value.editorState.value
        val parentId = _viewState.value.currentNode?.categoryId ?: 0
        viewModelScope.launch {
            resultOf {
                categoryRepository.addCategory(
                    category = category,
                    parentId = parentId,
                    transactionTypeCode = editorTypeCode.name
                )
            }.fold(
                onSuccess = {
                    _viewState.update {
                        it.copy(
                            editorState = CategoryEditorViewState.EditorState()
                        )
                    }
                    toggleLoading(false)
                },
                onFailure = {
                    toggleLoading(false)
                }
            )
        }
    }

    private fun closeEditor() {
        _viewState.update {
            it.copy(
                editorState = it.editorState.copy(
                    isOpened = false,
                    value = ""
                )
            )
        }
    }

    fun updateEditorValue(newValue: String) {
        _viewState.update {
            it.copy(
                editorState = it.editorState.copy(
                    value = newValue
                )
            )
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}

internal data class CategoryEditorViewState(
    val categoryTree: TransactionCategories = TransactionCategories(items = listOf()),
    val currentNode: TransactionCategories.Node? = null,
    val isLoading: Boolean = false,
    val editorState: EditorState = EditorState(),
    val dialogState: DeleteDialogState? = null
) {
    data class EditorState(
        val isOpened: Boolean = false,
        val value: String = ""
    )

    data class DeleteDialogState(
        val categoryId: Int
    )

}