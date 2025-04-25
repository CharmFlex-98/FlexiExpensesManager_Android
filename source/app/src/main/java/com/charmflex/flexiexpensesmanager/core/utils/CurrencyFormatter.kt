package com.charmflex.flexiexpensesmanager.core.utils

import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject
import kotlin.math.pow

internal interface CurrencyFormatter {
    fun format(amountInCent: Long, currencyCode: String): String
    fun formatTo(minorUnitAmount: Long, currencyCode: String, fromCurrencyCode: String, rate: Float): String
}

internal class CurrencyFormatterImpl @Inject constructor() : CurrencyFormatter{
    override fun format(amountInCent: Long, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance()
        val currencyInstance = Currency.getInstance(currencyCode)
        format.apply {
            maximumFractionDigits = currencyInstance.defaultFractionDigits
            currency = currencyInstance
        }

        val divFactor = 10.0.pow(currencyInstance.defaultFractionDigits.toDouble());
        return format.format(amountInCent.toBigDecimal().divide(divFactor.toBigDecimal()))
    }

    override fun formatTo(minorUnitAmount: Long, currencyCode: String, fromCurrencyCode: String, rate: Float): String {
        val format = NumberFormat.getCurrencyInstance()
        val currencyInstance = Currency.getInstance(currencyCode)
        val fromCurrencyInstance = Currency.getInstance(fromCurrencyCode)
        format.apply {
            maximumFractionDigits = currencyInstance.defaultFractionDigits
            currency = currencyInstance
        }

        val divFactor = 10.0.pow(fromCurrencyInstance.defaultFractionDigits.toDouble());
        return format.format(minorUnitAmount.toBigDecimal().divide(divFactor.toBigDecimal()).multiply(rate.toBigDecimal()))
    }
}

fun main() {
    val f = CurrencyFormatterImpl()
    print(f.format(1234, "KRW"))
}