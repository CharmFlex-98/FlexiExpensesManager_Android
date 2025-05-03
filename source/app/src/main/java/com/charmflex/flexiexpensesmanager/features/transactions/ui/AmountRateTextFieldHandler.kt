package com.charmflex.flexiexpensesmanager.features.transactions.ui

import com.charmflex.flexiexpensesmanager.core.domain.FEField
import com.charmflex.flexiexpensesmanager.features.transactions.domain.model.TransactionType
import com.charmflex.flexiexpensesmanager.features.transactions.ui.new_transaction.TransactionEditorViewState

internal interface AmountRateTextFieldHandler {
    suspend fun handleOnChangedEvent(
        field: FEField,
        viewState: TransactionEditorViewState,
        transactionType: TransactionType,
        onHandled: (FEField, String) -> Unit
    )
}

///**
// * UseCase: EXPENSES
// * The transaction amount in target currency.
// * If this changed,
// * the rate should reset based on currencies
// * the target account value should change based on the new reset currency
// */
//internal class TransactionAmountHandler(
//    private val accountRepository: AccountRepository,
//    private val currencyService: CurrencyService,
//    private val currencyVisualTransformationBuilder: CurrencyVisualTransformation.Builder
//) : AmountRateTextFieldHandler {
//    override suspend fun handleOnChangedEvent(
//        field: FEField,
//        viewState: TransactionEditorViewState,
//        transactionType: TransactionType,
//        onHandled: (FEField, String) -> Unit
//    ) {
//        if (transactionType == TransactionType.EXPENSES) {
//            val fromAccountField = viewState.fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }
//            val rateField = viewState.transactionToAccountCurrencyExchange?.rate
//            val transactionCurrencyField =
//                viewState.fields.firstOrNull { it.id == TRANSACTION_CURRENCY }
//            val accountTransactionAmountField = viewState.transactionToAccountCurrencyExchange?.toCurrency?.value
//
//            // Must have spending account name.
//            if (fromAccountField?.valueItem?.value.isNullOrBlank()
//                || transactionCurrencyField?.valueItem?.value.isNullOrBlank()
//                || field.valueItem.value.isBlank()
//            ) {
//                return
//            }
//
//            val account = accountRepository.getAccountById(fromAccountField!!.valueItem.id.toInt())
//            val transactionCurrency = transactionCurrencyField!!.valueItem.value
//
//            val newRate = currencyService.getCurrencyRate(transactionCurrency, account.currency)
//            onHandled(rateField!!, newRate.rate.toString())
//            val currencyFormatter = currencyVisualTransformationBuilder.create(account.currency)
//            onHandled(
//                accountTransactionAmountField!!,
//                currencyFormatter.getFormattedText(AnnotatedString((newRate.rate * field.valueItem.value.toLong()).toString()))
//            )
//        }
//    }
//}
//
/////**
//// * If this changed,
//// * Should only change the target account value
//// */
////internal class TransactionRateHandler(
////) : AmountRateTextFieldHandler {
////    override suspend fun handleOnChangedEvent(
////        viewState: TransactionEditorViewState,
////        transactionType: TransactionType,
////        onHandled: (FEField, String) -> Unit
////    ) {
////        val transactionRateField = viewState.fields.firstOrNull { it.id == TRANSACTION_RATE }
////        val fromAccountField = viewState.fields.firstOrNull { it.id == TRANSACTION_FROM_ACCOUNT }
////        val transactionAmountField = viewState.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }
////
////
////        if (fromAccountField?.valueItem?.value.isNullOrBlank()
////            || transactionRateField?.valueItem?.value.isNullOrBlank()
////            || transactionAmountField?.valueItem?.value.isNullOrBlank()
////        ) {
////            return
////        }
////
////        val newAmount =
////            transactionAmountField!!.valueItem.value.toLong() * transactionRateField!!.valueItem.value.toLong()
////
////        onHandled(fromAccountField!!, newAmount.toString())
////    }
////}
////
/////**
//// * If this changed,
//// * should change the rate according to the transaction amount
//// */
////internal class FromAccountTransactionAmount() : AmountRateTextFieldHandler {
////    override suspend fun handleOnChangedEvent(
////        viewState: TransactionEditorViewState,
////        transactionType: TransactionType,
////        onHandled: (FEField, String) -> Unit
////    ) {
////        val fromAccountAmountField =
////            viewState.fields.firstOrNull { it.id == ACCOUNT_TRANSACTION_AMOUNT }
////        val transactionAmountField = viewState.fields.firstOrNull { it.id == TRANSACTION_AMOUNT }
////        val transactionRateField = viewState.fields.firstOrNull { it.id == TRANSACTION_RATE }
////
////        if (fromAccountAmountField?.valueItem?.value.isNullOrBlank()
////            || transactionAmountField?.valueItem?.value.isNullOrBlank()
////            || transactionRateField == null
////        ) {
////            return;
////        }
////
////        val newRate = fromAccountAmountField!!.valueItem.value.toLong()
////            .div(transactionAmountField!!.valueItem.value.toLong())
////        onHandled(transactionRateField, newRate.toString())
////    }
////}
////
////
/////**
//// * If this changed,
//// * should change the rate according to the transaction amount
//// */
////internal class ToAccountTransactionAmount(
////    private val accountRepository: AccountRepository,
////    private val currencyService: CurrencyService,
////    priv
////) : AmountRateTextFieldHandler {
////    override suspend fun handleOnChangedEvent(
////        viewState: TransactionEditorViewState,
////        transactionType: TransactionType,
////        onHandled: (FEField, String) -> Unit
////    ) {
////        val toAccountAmountField =
////            viewState.fields.firstOrNull { it.id == ACCOUNT_TRANSACTION_AMOUNT }
////        val toAccountField = viewState.fields.firstOrNull { it.id == TRANSACTION_TO_ACCOUNT }
////        val primaryTransactionAmountField =
////            viewState.fields.firstOrNull { it.id == PRIMARY_TRANSACTION_AMOUNT }
////        val transactionRateField = viewState.fields.firstOrNull { it.id == TRANSACTION_RATE }
////
////        if (toAccountField?.valueItem?.value.isNullOrBlank()) return
////
////        if (transactionType == TransactionType.INCOME) {
////            if (toAccountAmountField?.valueItem?.value.isNullOrBlank()
////                || primaryTransactionAmountField?.valueItem?.value.isNullOrBlank()
////            ) {
////                return
////            }
////            val account = accountRepository.getAccountById(toAccountField!!.valueItem.value.toInt())
////            val rate = currencyService.getCurrencyRate()
////        }
////
////        if (transactionType == TransactionType.TRANSFER) {
////            if (toAccountAmountField?.valueItem?.value.isNullOrBlank()) {
////                return
////            }
////        }
////
////        if (toAccountAmountField?.valueItem?.value.isNullOrBlank()
////            || transactionAmountField?.valueItem?.value.isNullOrBlank()
////            || transactionRateField == null
////        ) {
////            return;
////        }
////
////        val newRate = toAccountAmountField!!.valueItem.value.toLong()
////            .div(transactionAmountField!!.valueItem.value.toLong())
////        onHandled(transactionRateField, newRate.toString())
////    }
////}