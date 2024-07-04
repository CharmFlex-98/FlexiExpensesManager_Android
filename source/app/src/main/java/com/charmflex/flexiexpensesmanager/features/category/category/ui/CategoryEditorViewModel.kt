package com.charmflex.flexiexpensesmanager.features.category.category.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.routes.BackupRoutes
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionCategories
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CategoryEditorViewModel @Inject constructor(
    private val categoryRepository: TransactionCategoryRepository,
    private val routeNavigator: RouteNavigator
) : ViewModel() {
    private var editorTypeCode: TransactionType = TransactionType.EXPENSES
    private var isImportFixFlow: Boolean = false

    private val _snackBarState: MutableStateFlow<SnackBarState> =
        MutableStateFlow(SnackBarState.None)
    val snackBarState = _snackBarState.asStateFlow()

    private val _viewState = MutableStateFlow(CategoryEditorViewState())
    val viewState = _viewState.asStateFlow()

    fun setType(type: String, importFixCategoryNames: String?) {
        editorTypeCode = when {
            type == TransactionType.INCOME.name -> TransactionType.INCOME
            type == TransactionType.EXPENSES.name -> TransactionType.EXPENSES
            else -> TransactionType.EXPENSES
        }

        if (importFixCategoryNames != null) {
            isImportFixFlow = true
            autoAddCategoryForImport(importFixCategoryNames.split("-->"))
        } else {
            listenCategoryList()
        }
    }

    private fun listenCategoryList() {
        viewModelScope.launch {
            categoryRepository.getCategories(editorTypeCode.name).collectLatest {
                toggleLoading(true)
                updateCategoryChain(it)
                toggleLoading(false)
            }
        }
    }

    private fun updateCategoryChain(it: TransactionCategories) {
        val categoryChain = _viewState.value.categoryChain.copy(
            version = _viewState.value.categoryChain.version + 1,
            categoryTree = it
        )
        _viewState.update { vs ->
            vs.copy(
                categoryChain = categoryChain,
                historyStack = run {
                    val historyStack = _viewState.value.historyStack
                    if (historyStack.isNotEmpty()) {
                        val firstOldNode = historyStack[0]
                        val firstNewNode =
                            it.items.firstOrNull { it.categoryId == firstOldNode.categoryId }
                        firstNewNode?.let {
                            val arrayDeque = listOf(it)
                            invalidateHistoryStack(0, arrayDeque)
                        } ?: historyStack
                    } else historyStack
                }
            )
        }

    }

    private fun invalidateHistoryStack(
        stackIndex: Int,
        result: List<TransactionCategories.Node>
    ): List<TransactionCategories.Node> {
        val historyStack = _viewState.value.historyStack
        // If reach the end, return
        if (stackIndex >= historyStack.size - 1) return result

        // Get current referencing node
        val oldNextNode = historyStack[stackIndex + 1]
        val newCurrentNode = result.last()
        val newNextNode =
            newCurrentNode.childNodes.firstOrNull { it.categoryId == oldNextNode.categoryId }
        if (newNextNode == null) return result// This should not happen..., but will return anyway

        return invalidateHistoryStack(stackIndex + 1, result + newNextNode)
    }

    private fun autoAddCategoryForImport(categoryChain: List<String>) {
        viewModelScope.launch {
            toggleLoading(true)
            categoryRepository.getCategories(editorTypeCode.name).firstOrNull()?.let {
                val rootCategoryName = categoryChain[0]
                val rootNode = it.items.firstOrNull { it.categoryName == rootCategoryName }
                if (rootNode != null) {
                    val arrayDeque = listOf(rootNode)
                    val state = getImportCategoryState(categoryChain, 0, arrayDeque)
                    _viewState.value = _viewState.value.copy(
                        categoryChain = _viewState.value.categoryChain.copy(
                            version = _viewState.value.categoryChain.version + 1,
                            categoryTree = it
                        ),
                        editorState = state.editorState,
                        historyStack = state.historyStack
                    )
                } else {
                    _viewState.value = _viewState.value.copy(
                        categoryChain = CategoryEditorViewState.CategoryChainUI(
                            version = _viewState.value.categoryChain.version + 1,
                            categoryTree = it
                        ),
                        editorState = CategoryEditorViewState.EditorState(
                            isOpened = true,
                            value = rootCategoryName
                        ),
                        historyStack = listOf()
                    )
                }

            }
            toggleLoading(false)
        }
    }

    fun back() {
        when {
            isImportFixFlow || viewState.value.currentNode == null -> routeNavigator.pop()
            _viewState.value.editorState.isOpened -> closeEditor()
            else -> {
                _viewState.value = _viewState.value.copy(
                    historyStack = _viewState.value.historyStack.dropLast(1)
                )
            }
        }
    }


    private fun getImportCategoryState(
        chain: List<String>,
        index: Int,
        nodeStack: List<TransactionCategories.Node>,
    ): ImportCategoryState {
        // If already reach the end of the chain but still able to get to here, something is wrong..
        if (index >= chain.size - 1) {
            return ImportCategoryState(
                historyStack = listOf(),
                errorMessage = "Cannot find the category path..."
            )
        }

        val currentNode = nodeStack.last()
        val nextCategoryName = chain[index + 1]
        val nextNode = currentNode.childNodes.firstOrNull { it.categoryName == nextCategoryName }

        return if (nextNode == null) {
            ImportCategoryState(
                historyStack = nodeStack,
                editorState = CategoryEditorViewState.EditorState(
                    isOpened = true,
                    value = nextCategoryName
                )
            )
        } else {
            getImportCategoryState(chain, index + 1, nodeStack + nextNode)
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
        if (_viewState.value.historyStack.size < categoryLevelCount() - 1) {
            _viewState.update {
                it.copy(
                    historyStack = _viewState.value.historyStack + node
                )
            }
        }
    }

    private fun categoryLevelCount(): Int = 3

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
                    // If this is import flow, just pop
                    if (isImportFixFlow) {
                        routeNavigator.popWithArguments(
                            mapOf(
                                BackupRoutes.Args.UPDATE_IMPORT_DATA to true
                            )
                        )
                        return@fold
                    }


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
    val categoryChain: CategoryChainUI = CategoryChainUI(),
    val historyStack: List<TransactionCategories.Node> = emptyList(),
    val isLoading: Boolean = false,
    val editorState: EditorState = EditorState(),
    val dialogState: DeleteDialogState? = null
) {
    val currentNode get() = historyStack.lastOrNull()

    data class CategoryChainUI(
        val version: Int = 1,
        val categoryTree: TransactionCategories = TransactionCategories(items = listOf())
    )

    data class EditorState(
        val isOpened: Boolean = false,
        val value: String = ""
    )

    data class DeleteDialogState(
        val categoryId: Int
    )
}

internal data class ImportCategoryState(
    val editorState: CategoryEditorViewState.EditorState = CategoryEditorViewState.EditorState(),
    val historyStack: List<TransactionCategories.Node>,
    val errorMessage: String = ""
)