package com.charmflex.flexiexpensesmanager.core.utils

import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject

internal interface CurrencyFormatter {
    fun format(amountInCent: Long, currencyCode: String): String
}

internal class CurrencyFormatterImpl @Inject constructor() : CurrencyFormatter{
    override fun format(amountInCent: Long, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance()
        format.apply {
            maximumFractionDigits = 2
            currency = Currency.getInstance(currencyCode)
        }

        return format.format(amountInCent.toBigDecimal().divide(100.toBigDecimal()))
    }
}

fun main() {
    val f = CurrencyFormatterImpl()
    print(f.format(1234, "KRW"))
}