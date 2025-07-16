package com.example.moneymanager.model

data class ExpenseCategory(
    val name: String,
    val amount: Float,
    val color: Int,
    val iconRes: Int,
    val createdAt: Long = System.currentTimeMillis()
    )
