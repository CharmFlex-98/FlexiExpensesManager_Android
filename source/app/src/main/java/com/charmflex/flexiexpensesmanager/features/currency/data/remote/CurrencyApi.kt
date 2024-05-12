package com.charmflex.flexiexpensesmanager.features.currency.data.remote

import com.charmflex.flexiexpensesmanager.features.currency.data.models.CurrencyRateResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


internal interface CurrencyApi {
    @Headers("Content-Type: application/json")
    @GET("https://api.fxratesapi.com/latest")
    suspend fun getCurrencyRate(): CurrencyRateResponse
}