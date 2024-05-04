package com.charmflex.flexiexpensesmanager.features.transactions.ui.transaction_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.resultOf
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.Transaction
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class TransactionDetailViewModel(
    private val transactionId: Long,
    private val routeNavigator: RouteNavigator,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(TransactionDetailViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadDetail()
    }

    class Factory @Inject constructor(
        private val routeNavigator: RouteNavigator,
        private val transactionRepository: TransactionRepository
    ) {
        fun create(transactionId: Long): TransactionDetailViewModel {
            return TransactionDetailViewModel(
                transactionId = transactionId,
                routeNavigator = routeNavigator,
                transactionRepository = transactionRepository
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
                    Log.d("test", it.toString())
                    toggleLoader(false)
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

    fun onBack() {
        routeNavigator.pop()
    }
}

internal data class TransactionDetailViewState(
    val detail: Transaction? = null,
    val isLoading: Boolean = false
)