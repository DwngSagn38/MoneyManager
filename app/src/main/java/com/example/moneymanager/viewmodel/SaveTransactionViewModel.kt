package com.example.moneymanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.database.AppDatabase
import com.example.moneymanager.model.ExpenseCategory
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val _totalBalance = MutableStateFlow(0f)
    val totalBalance: StateFlow<Float> = _totalBalance

    private val _expenses = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val expenses: StateFlow<List<TransactionEntity>> = _expenses

    private val _totalExpenses = MutableStateFlow(0f)
    val totalExpenses: StateFlow<Float> = _totalExpenses

    private val _incomes = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val incomes: StateFlow<List<TransactionEntity>> = _incomes

    private val _totalIncome = MutableStateFlow(0f)
    val totalIncome: StateFlow<Float> = _totalIncome

    private val _loans = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val loans: StateFlow<List<TransactionEntity>> = _loans

    private val _totalLoans = MutableStateFlow(0f)
    val totalLoans: StateFlow<Float> = _totalLoans

    private val _totalBorrowed = MutableStateFlow(0f)
    val totalBorrowed: StateFlow<Float> = _totalBorrowed

    init {
        fetchAllTransactions()
        viewModelScope.launch {
            combine(
                _totalIncome,
                _totalExpenses,
                _totalLoans,
                _totalBorrowed,
            ) { income, expenses, loans, borrowed ->
                income - expenses + (loans - borrowed)
            }.collect { balance ->
                _totalBalance.value = balance
                Log.d("SaveTransactionVM", "Total balance updated: $balance")
            }
        }
    }
    suspend fun getTransactionsByYear(year: Int): List<TransactionEntity> {
        val all = dao.getAllTransactions()

        return all.filter { transaction ->
            try {
                val parts = transaction.date.split("/") // dd/MM/yyyy
                if (parts.size != 3) return@filter false

                val itemYear = parts[2].toInt()
                itemYear == year
            } catch (e: Exception) {
                false
            }
        }
    }


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
            val list :List<TransactionEntity> = dao.getTransactionsByType(type)
            when (type) {
                "Expense" -> {
                    _expenses.value = list
                    _totalExpenses.value = list.sumOf { it.amount.toDouble() }.toFloat()
                }
                "Income" -> {
                    _incomes.value = list
                    _totalIncome.value = list.sumOf { it.amount.toDouble() }.toFloat()
                }
                "Loans" ->{
                    _loans.value = list
                    _totalLoans.value = list.filter { it.check == true }.sumOf { it.amount.toDouble() }.toFloat()
                    _totalBorrowed.value = list.filter { it.check == false }.sumOf { it.amount.toDouble() }.toFloat()
                }
            }
        }
    }

    private val _twoMonthsTransactions = MutableLiveData<List<TransactionEntity>>()
    val twoMonthsTransactions: LiveData<List<TransactionEntity>> get() = _twoMonthsTransactions

    fun filterTransactionsForTwoMonths(month: Int, year: Int, name: String, sortByYear: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getAllTransactions()

            val filtered = all.filter { transaction ->
                val parts = transaction.date.split("/") // dd/MM/yyyy
                if (parts.size != 3) return@filter false

                val itemMonth = parts[1].toIntOrNull() ?: return@filter false
                val itemYear = parts[2].toIntOrNull() ?: return@filter false

                if (sortByYear) {
                    // Lọc theo name, và trong năm hiện tại hoặc năm trước
                    (itemYear == year || itemYear == year - 1) && transaction.name == name
                } else {
                    // Lọc theo name, trong tháng hiện tại hoặc tháng trước
                    val thisMonth = month + 1
                    val lastMonth = if (thisMonth == 1) 12 else thisMonth - 1
                    val lastMonthYear = if (thisMonth == 1) year - 1 else year

                    (itemYear == year && itemMonth == thisMonth ||
                            itemYear == lastMonthYear && itemMonth == lastMonth)
                            && transaction.name == name
                }
            }

            Log.d("SaveTransactionVM", "Filtered transactions: $filtered")
            _twoMonthsTransactions.postValue(filtered)
        }
    }
}
