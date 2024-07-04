package com.charmflex.flexiexpensesmanager.core.utils

internal interface Mapper<T, U> {
    fun map(from: T): U
}

internal interface SuspendableMapper<T, U> {
    suspend fun map(from: T): U
}