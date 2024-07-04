package com.charmflex.flexiexpensesmanager.core.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException


inline fun <T> resultOf(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        // Rethrow for cancellation exception because coroutine need to catch this for proper cancellation
        throw cancellation
    } catch (e: Throwable) {
        Result.failure(exception = e)
    }
}

fun <T> unwrapResult(result: Result<T>): T {
    return result.getOrThrow()
}

const val DEFAULT_DATE_TIME_PATTERN = "dd MMMM yyyy hh:mm a"
const val DATE_ONLY_DEFAULT_PATTERN = "yyyy-MM-dd"
const val MONTH_ONLY_DEFAULT_PATTERN = "MMMM"
const val YEAR_ONLY_DEFAULT_PATTERN = "yyyy"
const val MONTH_YEAR_PATTERN = "MMMM yyyy"
const val MONTH_YEAR_DB_PATTERN = "MMMM/yyyy"
const val SHORT_MONTH_YEAR_PATTERN = "MMM yyyy"
const val TIME_ONLY_DEFAULT_PATTERN = "hh:mm a"


// TO CLIENT
fun String.toLocalDateTime(pattern: String): LocalDateTime? {
    if (this.isEmpty()) return null
    return LocalDateTime.parse(this, getDateTimeFormatter(pattern))
}

fun String.toLocalDate(pattern: String): LocalDate? {
    if (this.isEmpty()) return null
    return LocalDate.parse(this, getDateTimeFormatter(pattern))
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate?.toStringWithPattern(pattern: String): String {
    return this?.format(getDateTimeFormatter(pattern)) ?: ""
}

fun LocalDateTime?.toStringWithPattern(pattern: String): String {
    return this?.format(getDateTimeFormatter(pattern)) ?: ""
}

fun LocalDateTime?.toISO8601String(zoneId: ZoneId): String {
    return this?.atZone(zoneId)?.toInstant().toString()
}

fun String.fromISOToStringWithPattern(pattern: String): String {
    return fromISOToLocalDateTime().toStringWithPattern(pattern = pattern)
}

// INTERNAL

private fun getDateTimeFormatter(pattern: String): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(pattern, Locale.US)
}

fun String.fromISOToLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.parse(this), ZoneId.systemDefault())
}