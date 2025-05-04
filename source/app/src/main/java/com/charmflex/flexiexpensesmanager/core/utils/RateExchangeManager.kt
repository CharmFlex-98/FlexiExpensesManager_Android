package com.charmflex.flexiexpensesmanager.core.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.pow

internal class RateExchangeManager @Inject constructor() {
    fun convertTo(minorUnitAmount: Long, currencyCode: String, fromCurrencyCode: String, rate: Float): String {
        val currencyInstance = Currency.getInstance(currencyCode)
        val fromCurrencyInstance = Currency.getInstance(fromCurrencyCode)

        val divFactor = 10.0.pow(fromCurrencyInstance.defaultFractionDigits.toDouble())

        val res = minorUnitAmount.toBigDecimal().divide(divFactor.toBigDecimal()).multiply(rate.toBigDecimal())
        val minorAmount = res.multiply(BigDecimal.TEN.pow(currencyInstance.defaultFractionDigits))
            .setScale(0, RoundingMode.HALF_EVEN)
            .toPlainString()

        return minorAmount
    }

    fun getRate(fromCurrency: String, minorUnitAmountFrom: Long, toCurrency: String, minorUnitAmountTo: Long): Float {
        val fromCurrencyInstance = Currency.getInstance(fromCurrency)
        val toCurrencyInstance = Currency.getInstance(toCurrency)

        val fromDivFactor = 10.0.pow(fromCurrencyInstance.defaultFractionDigits.toDouble())
        val fromMajorAmount = minorUnitAmountFrom.toBigDecimal().divide(fromDivFactor.toBigDecimal())

        val toDivFactor = 10.0.pow(toCurrencyInstance.defaultFractionDigits.toDouble())
        val toMajorAmount = minorUnitAmountTo.toBigDecimal().divide(toDivFactor.toBigDecimal())

        return toMajorAmount.divide(fromMajorAmount, 9, RoundingMode.HALF_EVEN)
            .toFloat()
    }
}