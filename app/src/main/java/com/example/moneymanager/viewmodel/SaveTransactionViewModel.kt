package com.example.moneymanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.database.AppDatabase
import com.example.moneymanager.model.ExpenseCategory
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SaveTransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).transactionDao()

    val selectedDate = MutableLiveData<Triple<Int, Int, Boolean>>()

    // To observe all transactions
    private val _allTransactions = MutableLiveData<List<TransactionEntity>>()
    val allTransactions: LiveData<List<TransactionEntity>> get() = _allTransactions

    // For filtered results
    private val _filteredTransactions = MutableLiveData<List<TransactionEntity>>()
    val filteredTransactions: LiveData<List<TransactionEntity>> get() = _filteredTransactions

    init {
        fetchAllTransactions()

    }

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
            Log.d("SaveTransactionVM", "Transaction inserted: $transaction")
            filterTransactionsForCurrentDate()
        }
    }

    private fun filterTransactionsForCurrentDate() {
        selectedDate.value?.let { (month, year, sortByYear) ->
            filterTransactions(month, year, sortByYear)
        }
    }


    private fun fetchAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getAllTransactions()
            _allTransactions.postValue(all)
        }
    }

    fun filterTransactions(month: Int, year: Int, sortByYear: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getAllTransactions()
            Log.d("SaveTransactionVM", "All transactions: $all")

            val filtered = all.filter { transaction ->
                try {
                    val parts = transaction.date.split("/") // dd/MM/yyyy
                    if (parts.size != 3) return@filter false

                    val itemMonth = parts[1].toInt()
                    val itemYear = parts[2].toInt()

                    if (sortByYear) {
                        itemYear == year
                    } else {
                        itemYear == year && itemMonth == (month + 1)
                    }
                } catch (e: Exception) {
                    false
                }
            }

            _filteredTransactions.postValue(filtered)
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
