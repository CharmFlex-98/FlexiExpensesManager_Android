package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetUserCurrencyUseCase
import com.charmflex.flexiexpensesmanager.features.scheduler.di.modules.TransactionEditorProvider
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.transactions.domain.repositories.TransactionRepository
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.usecases.SubmitTransactionUseCase
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

internal class TransactionEditorViewModel @Inject constructor(
    private val transactionId: Long?,
    private val transactionRepository: TransactionRepository,
    private val submitTransactionUseCase: SubmitTransactionUseCase,
    contentProvider: TransactionEditorContentProvider,
    accountRepository: AccountRepository,
    routeNavigator: RouteNavigator,
    transactionCategoryRepository: TransactionCategoryRepository,
    currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    getUserCurrencyUseCase: GetUserCurrencyUseCase,
    tagRepository: TagRepository,
) : TransactionEditorBaseViewModel(
    contentProvider,
    accountRepository,
    routeNavigator,
    transactionCategoryRepository,
    currencyVisualTransformationBuilder,
    getUserCurrencyUseCase,
    tagRepository,
    transactionId
) {
    class Factory @Inject constructor(
        @TransactionEditorProvider(TransactionEditorProvider.Type.DEFAULT)
        private val contentProvider: TransactionEditorContentProvider,
        private val accountRepository: AccountRepository,
        private val transactionRepository: TransactionRepository,
        private val routeNavigator: RouteNavigator,
        private val transactionCategoryRepository: TransactionCategoryRepository,
        private val submitTransactionUseCase: SubmitTransactionUseCase,
        private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
        private val getUserCurrencyUseCase: GetUserCurrencyUseCase,
        private val tagRepository: TagRepository,
    ) {
        fun create(transactionId: Long?): TransactionEditorViewModel {
            return TransactionEditorViewModel(
                transactionId,
                transactionRepository,
                submitTransactionUseCase,
                contentProvider,
                accountRepository,
                routeNavigator,
                transactionCategoryRepository,
                currencyVisualTransformationBuilder,
                getUserCurrencyUseCase,
                tagRepository,
            )
        }
    }

    init {
        initialise()
    }

    override fun calendarSelectionRange(): ClosedRange<LocalDate> {
        return LocalDate.now().minusYears(10)..LocalDate.now()
    }

    override suspend fun loadTransaction(id: Long): TransactionEditorDataUI? {
        val res = transactionRepository.getTransactionById(id).firstOrNull() ?: return null
        return TransactionEditorDataUI(
            res.transactionName,
            res.transactionAccountFrom,
            res.transactionAccountTo,
            res.transactionTypeCode,
            res.amountInCent,
            res.currency,
            res.rate,
            res.transactionDate,
            res.transactionCategory,
            res.tags
        )
    }

    override suspend fun submitExpenses(
        id: Long?,
        name: String,
        fromAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitExpenses(
            id, name, fromAccountId, amount, categoryId, transactionDate, currency, rate, tagIds
        )
    }

    override suspend fun submitIncome(
        id: Long?,
        name: String,
        toAccountId: Int,
        amount: Long,
        categoryId: Int,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitIncome(
            id, name, toAccountId, amount, categoryId, transactionDate, currency, rate
        )
    }

    override suspend fun submitTransfer(
        id: Long?,
        name: String,
        fromAccountId: Int,
        toAccountId: Int,
        amount: Long,
        transactionDate: String,
        currency: String,
        rate: Float,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitTransfer(
            id, name, fromAccountId, toAccountId, amount, transactionDate, currency, rate
        )
    }

    override fun getType(): TransactionRecordableType {
        return transactionId?.let {
            TransactionRecordableType.EDIT_TRANSACTION
        } ?: TransactionRecordableType.NEW_TRANSACTION
    }
}