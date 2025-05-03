package com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction

import com.charmflex.flexiexpensesmanager.R
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.RateExchangeManager
import com.charmflex.flexiexpensesmanager.core.utils.ResourcesProvider
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import com.charmflex.flexiexpensesmanager.features.scheduler.di.modules.TransactionEditorProvider
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.currency.domain.repositories.UserCurrencyRepository
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
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
    tagRepository: TagRepository,
    currencyService: CurrencyService,
    currencyFormatter: CurrencyFormatter,
    userCurrencyRepository: UserCurrencyRepository,
    rateExchangeManager: RateExchangeManager,
    private val resourcesProvider: ResourcesProvider
) : TransactionEditorBaseViewModel(
    contentProvider,
    accountRepository,
    routeNavigator,
    transactionCategoryRepository,
    currencyVisualTransformationBuilder,
    tagRepository,
    currencyService,
    currencyFormatter,
    rateExchangeManager,
    userCurrencyRepository,
    transactionId,
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
        private val tagRepository: TagRepository,
        private val resourcesProvider: ResourcesProvider,
        private val currencyService: CurrencyService,
        private val currencyFormatter: CurrencyFormatter,
        private val userCurrencyRepository: UserCurrencyRepository,
        private val rateExchangeManager: RateExchangeManager
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
                tagRepository,
                currencyService,
                currencyFormatter,
                userCurrencyRepository,
                rateExchangeManager,
                resourcesProvider,
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
            res.minorUnitAmount,
            res.accountMinorUnitAmount,
            res.primaryMinorUnitAmount,
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
        accountCurrencyRate: Float,
        primaryCurrencyRate: Float?,
        accountMinorUnitAmount: Long,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitExpenses(
            id,
            name,
            fromAccountId,
            amount,
            categoryId,
            transactionDate,
            currency,
            accountCurrencyRate,
            primaryCurrencyRate,
            accountMinorUnitAmount,
            primaryMinorUnitAmount,
            tagIds
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
        primaryCurrencyRate: Float?,
        primaryMinorUnitAmount: Long,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitIncome(
            id,
            name,
            toAccountId,
            amount,
            categoryId,
            transactionDate,
            currency,
            primaryCurrencyRate,
            primaryMinorUnitAmount
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
        accountCurrencyRate: Float,
        accountMinorUnitAmount: Long,
        tagIds: List<Int>,
    ): Result<Unit> {
        return submitTransactionUseCase.submitTransfer(
            id,
            name,
            fromAccountId,
            toAccountId,
            amount,
            transactionDate,
            currency,
            accountCurrencyRate,
            accountMinorUnitAmount
        )
    }

    override suspend fun submitUpdate(
        id: Long?,
        accountId: Int,
        isIncrement: Boolean,
        amount: Long,
        transactionDate: String,
    ): Result<Unit> {
        val hint =
            if (isIncrement) UpdateAccountType.INCREMENT.name else UpdateAccountType.DEDUCTION.name
        val name = resourcesProvider.getString(R.string.generic_update_account)
        return submitTransactionUseCase.submitUpdateAccount(
            id, "$name ($hint)", accountId, isIncrement, amount, transactionDate
        )
    }

    override fun getType(): TransactionRecordableType {
        return transactionId?.let {
            TransactionRecordableType.EDIT_TRANSACTION
        } ?: TransactionRecordableType.NEW_TRANSACTION
    }
}