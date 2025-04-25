package com.charmflex.flexiexpensesmanager.features.scheduler.ui.scheduler_editor

import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.core.navigation.RouteNavigator
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyFormatter
import com.charmflex.flexiexpensesmanager.core.utils.CurrencyVisualTransformation
import com.charmflex.flexiexpensesmanager.core.utils.RateExchangeManager
import com.charmflex.flexiexpensesmanager.features.account.domain.repositories.AccountRepository
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyUseCase
import com.charmflex.flexiexpensesmanager.features.scheduler.di.modules.TransactionEditorProvider
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.models.SchedulerPeriod
import com.charmflex.flexiexpensesmanager.features.scheduler.domain.repository.TransactionSchedulerRepository
import com.charmflex.flexiexpensesmanager.features.scheduler.usecases.SubmitTransactionSchedulerUseCase
import com.charmflex.flexiexpensesmanager.features.tag.domain.repositories.TagRepository
import com.charmflex.flexiexpensesmanager.features.category.category.domain.repositories.TransactionCategoryRepository
import com.charmflex.flexiexpensesmanager.features.currency.service.CurrencyService
import com.charmflex.flexiexpensesmanager.features.currency.usecases.GetCurrencyRateUseCase
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TRANSACTION_SCHEDULER_PERIOD
import com.charmflex.flexiexpensesmanager.features.transactions.provider.TransactionEditorContentProvider
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionEditorBaseViewModel
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionEditorDataUI
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionRecordableType
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import javax.inject.Inject

internal class SchedulerEditorViewModel(
    private val schedulerId: Long?,
    private val transactionSchedulerRepository: TransactionSchedulerRepository,
    private val submitTransactionSchedulerUseCase: SubmitTransactionSchedulerUseCase,
    contentProvider: TransactionEditorContentProvider,
    accountRepository: AccountRepository,
    routeNavigator: RouteNavigator,
    transactionCategoryRepository: TransactionCategoryRepository,
    currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
    getCurrencyUseCase: GetCurrencyUseCase,
    getCurrencyRateUseCase: GetCurrencyRateUseCase,
    currencyService: CurrencyService,
    currencyFormatter: CurrencyFormatter,
    rateExchangeManager: RateExchangeManager,
    tagRepository: TagRepository,
) : TransactionEditorBaseViewModel(
    contentProvider,
    accountRepository,
    routeNavigator,
    transactionCategoryRepository,
    currencyVisualTransformationBuilder,
    getCurrencyUseCase,
    tagRepository,
    getCurrencyRateUseCase,
    currencyService,
    currencyFormatter,
    rateExchangeManager,
    schedulerId
) {

    class Factory @Inject constructor(
        @TransactionEditorProvider(TransactionEditorProvider.Type.SCHEDULER)
        private val contentProvider: TransactionEditorContentProvider,
        private val accountRepository: AccountRepository,
        private val transactionSchedulerRepository: TransactionSchedulerRepository,
        private val submitTransactionSchedulerUseCase: SubmitTransactionSchedulerUseCase,
        private val routeNavigator: RouteNavigator,
        private val transactionCategoryRepository: TransactionCategoryRepository,
        private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder,
        private val getCurrencyUseCase: GetCurrencyUseCase,
        private val getCurrencyRateUseCase: GetCurrencyRateUseCase,
        private val currencyService: CurrencyService,
        private val currencyFormatter: CurrencyFormatter,
        private val rateExchangeManager: RateExchangeManager,
        private val tagRepository: TagRepository,
    ) {
        fun create(schedulerId: Long?): SchedulerEditorViewModel {
            return SchedulerEditorViewModel(
                schedulerId,
                transactionSchedulerRepository,
                submitTransactionSchedulerUseCase,
                contentProvider,
                accountRepository,
                routeNavigator,
                transactionCategoryRepository,
                currencyVisualTransformationBuilder,
                getCurrencyUseCase,
                getCurrencyRateUseCase,
                currencyService,
                currencyFormatter,
                rateExchangeManager,
                tagRepository,
            )
        }
    }

    override val transactionType: List<TransactionType>
        get() = super.transactionType.filter { it != TransactionType.UPDATE_ACCOUNT }

    init {
        initialise()
    }

    private val selectedPeriod = MutableStateFlow(SchedulerPeriod.MONTHLY)

    override suspend fun loadTransaction(id: Long): TransactionEditorDataUI? {
        val res = transactionSchedulerRepository.getTransactionSchedulerById(id) ?: return null
        return TransactionEditorDataUI(
            res.transactionName,
            res.accountFrom,
            res.accountTo,
            res.transactionType.name,
            res.amountInCent,
            res.currency,
            res.rate,
            res.startUpdateDate,
            res.category,
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
        return submitTransactionSchedulerUseCase.submitExpenses(
            id,
            name,
            fromAccountId,
            amount,
            categoryId,
            transactionDate,
            transactionDate,
            currency,
            rate,
            tagIds,
            selectedPeriod.value
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
        return submitTransactionSchedulerUseCase.submitIncome(
            id,
            name,
            toAccountId,
            amount,
            categoryId,
            transactionDate,
            transactionDate,
            currency,
            rate,
            tagIds,
            selectedPeriod.value,
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
        return submitTransactionSchedulerUseCase.submitTransfer(
            id,
            name,
            fromAccountId,
            toAccountId,
            amount,
            transactionDate,
            transactionDate,
            currency,
            rate,
            selectedPeriod.value,
            tagIds
        )
    }

    override suspend fun submitUpdate(
        id: Long?,
        accountId: Int,
        isIncrement: Boolean,
        amount: Long,
        transactionDate: String,
    ): Result<Unit> {
        // Do nothing
        return Result.failure(Throwable("Should not be able to call here"))
    }

    override fun onPeriodSelected(period: SchedulerPeriod, targetField: FEField?) {
        super.onPeriodSelected(period, targetField)
        selectedPeriod.value = period
    }

    override fun calendarSelectionRange(): ClosedRange<LocalDate> {
        return LocalDate.now().minusYears(10)..LocalDate.now().plusMonths(3)
    }

    override fun allowProceed(): Boolean {
        return super.allowProceed() &&
                (viewState.value.fields.firstOrNull { it.id == TRANSACTION_SCHEDULER_PERIOD }?.id?.isNotBlank() ?: false)
    }

    override fun getType(): TransactionRecordableType {
        return schedulerId?.let {
            TransactionRecordableType.EDIT_SCHEDULER
        } ?: TransactionRecordableType.NEW_SCHEDULER
    }
}