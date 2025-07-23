package com.example.moneymanager.entity
data class ConverntorResponse(
    val rates: Map<String, Double>,
    val base: String,
    val timestamp: Long
)
