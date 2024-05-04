package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.navigation.popWithHomeRefresh
import com.charmflex.flexiexpensesmanager.core.navigation.routes.HomeRoutes
import com.charmflex.flexiexpensesmanager.core.utils.ResourcesProvider
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.ui_common.SnackBarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TransactionDetailViewModel(
    private val transactionId: Long,
    private val routeNavigator: RouteNavigator,
    private val transactionRepository: TransactionRepository,
    private val resourcesProvider: ResourcesProvider
) : ViewModel() {

    var snackBarState: MutableState<SnackBarState> = mutableStateOf(SnackBarState.None)
        private set
    private val _viewState = MutableStateFlow(TransactionDetailViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadDetail()
    }

    class Factory @Inject constructor(
        private val routeNavigator: RouteNavigator,
        private val transactionRepository: TransactionRepository,
        private val resourcesProvider: ResourcesProvider
    ) {
        fun create(transactionId: Long): TransactionDetailViewModel {
            return TransactionDetailViewModel(
                transactionId = transactionId,
                routeNavigator = routeNavigator,
                transactionRepository = transactionRepository,
                resourcesProvider = resourcesProvider
            )
        }
    }

    private fun loadDetail() {
        toggleLoader(false)
        viewModelScope.launch {
            resultOf {
                transactionRepository.getTransactionById(transactionId = transactionId)
            }.fold(
                onSuccess = { transaction ->
                    _viewState.update {
                        it.copy(
                            detail = transaction
                        )
                    }
                    toggleLoader(false)
                },
                onFailure = {
                    toggleLoader(false)
                }
            )
        }
    }

    fun openDeleteWarningDialog() {
        _viewState.update {
            it.copy(
                dialogState = TransactionDetailViewState.DeleteDialogState
            )
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            resultOf {
                transactionRepository.deleteTransactionById(transactionId)
            }.fold(
                onSuccess = {
                    _viewState.update {
                        it.copy(
                            dialogState = TransactionDetailViewState.SuccessDialog(
                                title = resourcesProvider.getString(R.string.generic_success),
                                subtitle = resourcesProvider.getString(R.string.delete_transaction_success_subtitle)
                            )
                        )
                    }
                },
                onFailure = {
                    snackBarState.value = SnackBarState.Error(
                        message = resourcesProvider.getString(R.string.generic_something_went_wron)
                    )
                }
            )
        }
    }

    private fun toggleLoader(toggle: Boolean) {
        _viewState.update {
            it.copy(
                isLoading = toggle
            )
        }
    }

    fun onCloseDialog() {
        _viewState.update {
            it.copy(
                dialogState = null
            )
        }
    }

    fun onBack(shouldRefresh: Boolean = false) {
        if (shouldRefresh) routeNavigator.popWithHomeRefresh()
        else routeNavigator.pop()
    }
}

internal data class TransactionDetailViewState(
    val detail: Transaction? = null,
    val isLoading: Boolean = false,
    val dialogState: DialogState? = null
) {
    sealed interface DialogState
    object DeleteDialogState : DialogState
    data class SuccessDialog(
        val title: String,
        val subtitle: String
    ) : DialogState
}