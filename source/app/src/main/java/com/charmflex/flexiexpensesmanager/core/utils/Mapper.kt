package com.charmflex.flexiexpensesmanager.core.utils

internal interface Mapper<T, U> {
    suspend fun map(from: T): U
}