package com.example.moneymanager.intenface

import com.example.moneymanager.entity.ConverntorResponse
import retrofit2.http.GET

interface ApiService {
    @GET("latest")
    suspend fun getCurrencyRates(): ConverntorResponse
}
