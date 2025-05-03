package com.charmflex.flexiexpensesmanager.core.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject
import kotlin.math.pow

internal interface CurrencyFormatter {
    fun format(minorUnitAmount: Long, currencyCode: String): String
    fun formatWithoutSymbol(minorUnitAmount: Long, currencyCode: String): String
    fun formatTo(minorUnitAmount: Long, currencyCode: String, fromCurrencyCode: String, rate: Float): String
    fun from(amountValue: Double, currencyCode: String): Long
}

internal class CurrencyFormatterImpl @Inject constructor() : CurrencyFormatter{
    override fun format(minorUnitAmount: Long, currencyCode: String): String {
        val amount = minorToMajor(minorUnitAmount, currencyCode)
        return getCurrencyFormatter(currencyCode).format(amount)
    }

    override fun formatWithoutSymbol(minorUnitAmount: Long, currencyCode: String): String {
        return minorToMajor(minorUnitAmount, currencyCode).toPlainString()
    }

    override fun formatTo(
        minorUnitAmount: Long,
        currencyCode: String,
        fromCurrencyCode: String,
        rate: Float
    ): String {
        val fromAmount = minorToMajor(minorUnitAmount, fromCurrencyCode)
        val converted = fromAmount.multiply(rate.toBigDecimal())
        return getCurrencyFormatter(currencyCode).format(converted)
    }

    override fun from(amountValue: Double, currencyCode: String): Long {
        val factor = getFactor(currencyCode)
        return (amountValue * factor).toLong()
    }

    private fun minorToMajor(minorUnitAmount: Long, currencyCode: String): BigDecimal {
        val factor = getFactor(currencyCode)
        return minorUnitAmount.toBigDecimal().divide(BigDecimal.valueOf(factor))
    }

    private fun getFactor(currencyCode: String): Double {
        val fractionDigits = Currency.getInstance(currencyCode).defaultFractionDigits
        return 10.0.pow(fractionDigits)
    }

    private fun getCurrencyFormatter(currencyCode: String): NumberFormat {
        return NumberFormat.getCurrencyInstance().apply {
            val currency = Currency.getInstance(currencyCode)
            maximumFractionDigits = currency.defaultFractionDigits
            this.currency = currency
        }
    }
}

fun main() {
    val f = CurrencyFormatterImpl()
    print(f.format(1234, "KRW"))
}