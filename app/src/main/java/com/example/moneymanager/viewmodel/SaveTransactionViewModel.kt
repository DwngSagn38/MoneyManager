package com.example.moneymanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.database.AppDatabase
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SaveTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).transactionDao()

    private val _expenses = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val expenses: StateFlow<List<TransactionEntity>> = _expenses

    private val _totalExpenses = MutableStateFlow(0f)
    val totalExpenses: StateFlow<Float> = _totalExpenses

    private val _incomes = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val incomes: StateFlow<List<TransactionEntity>> = _incomes

    private val _loans = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val loans: StateFlow<List<TransactionEntity>> = _loans


    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertTransaction(transaction)
            loadTransactionsByType(transaction.type)
        }
    }


    fun loadTransactionsByType(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getTransactionsByType(type)
            when (type) {
                "Expense" -> {
                    _expenses.value = list
                    _totalExpenses.value = list.sumOf { it.amount.toDouble() }.toFloat()
                }
                "Income" -> _incomes.value = list
                "Loan" -> _loans.value = list
            }
        }
    }

}
