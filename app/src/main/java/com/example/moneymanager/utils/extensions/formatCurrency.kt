package com.example.moneymanager.utils.extensions

import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double, currencyCode: String): String {
    return try {
        val currency = Currency.getInstance(currencyCode)
        val symbol = currency.symbol
        val format = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 0
        }
        "$symbol ${format.format(amount)}"
    } catch (e: Exception) {
        "$currencyCode ${String.format("%.2f", amount)}"
    }
}

