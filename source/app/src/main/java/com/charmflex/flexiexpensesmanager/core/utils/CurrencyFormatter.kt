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
        val data = formatToAmount(minorUnitAmount, currencyCode)
        val format = NumberFormat.getCurrencyInstance()
        return format.format(data)
    }

    override fun formatWithoutSymbol(minorUnitAmount: Long, currencyCode: String): String {
        return formatToAmount(minorUnitAmount, currencyCode).toPlainString()
    }

    private fun formatToAmount(minorUnitAmount: Long, currencyCode: String): BigDecimal {
        val divFactor = getFactor(currencyCode)
        return minorUnitAmount.toBigDecimal().divide(divFactor.toBigDecimal())
    }

    private fun getFactor(currencyCode: String): Double {
        val currencyInstance = Currency.getInstance(currencyCode)
        val divFactor = 10.0.pow(currencyInstance.defaultFractionDigits.toDouble())

        return divFactor;
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

    override fun from(amountValue: Double, currencyCode: String): Long {
        val timesFactor = getFactor(currencyCode)
        return amountValue.times(timesFactor).toLong()
    }
}

fun main() {
    val f = CurrencyFormatterImpl()
    print(f.format(1234, "KRW"))
}