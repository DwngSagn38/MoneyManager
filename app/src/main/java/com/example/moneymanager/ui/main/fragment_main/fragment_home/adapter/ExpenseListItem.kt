package com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter

import com.example.moneymanager.model.TransactionEntity

sealed class ExpenseListItem {
    data class DateHeader(val date: String, val totalAmount: Double) : ExpenseListItem()
    data class ExpenseItem(val transaction: TransactionEntity) : ExpenseListItem()
}
