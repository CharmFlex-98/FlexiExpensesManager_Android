package com.charmflex.flexiexpensesmanager.features.auth.data.remote

import androidx.room.Update
import com.charmflex.flexiexpensesmanager.features.auth.data.model.RegisterUserRequest
import com.charmflex.flexiexpensesmanager.features.auth.data.model.UpdateUserInfoRequest
import com.charmflex.flexiexpensesmanager.features.auth.data.model.UpdateUserInfoResponse
import com.charmflex.flexiexpensesmanager.features.currency.data.models.CurrencyRateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


internal interface AuthApi {
    @Headers("Content-Type: application/json")
    @PUT("/auth/users/login")
    suspend fun upsertUser(
        @Body updateUserInfoRequest: UpdateUserInfoRequest
    ) : UpdateUserInfoResponse
}