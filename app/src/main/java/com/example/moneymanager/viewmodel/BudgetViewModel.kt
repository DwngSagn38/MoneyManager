package com.example.moneymanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moneymanager.database.AppDatabase
import com.example.moneymanager.model.BudgetModel
import com.example.moneymanager.utils.extensions.collectInLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).budgetDao()
    val selectedDate = MutableLiveData<Triple<Int, Int, Boolean>>()

    private val transactionViewModel = SaveTransactionViewModel(application)

    private val _allBudget = MutableLiveData<List<BudgetModel>>()
    val allBudget: LiveData<List<BudgetModel>> get() = _allBudget

    init {
        fetchAllBudget()
    }

    private fun fetchAllBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getAllBudget()
            _allBudget.postValue(all)
        }
    }

    fun insertBudget(dateTime : String , budget : Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val budget = BudgetModel(dateTime = dateTime, budget = budget)
            Log.d("BudgetViewModel", "Inserting budget: $budget")

            dao.insertBudget(budget)
            fetchAllBudget()
        }
    }

    private val _budgetByDate = MutableLiveData<BudgetModel>()
    val budgetByDate: LiveData<BudgetModel> get() = _budgetByDate

    fun getBudgetByDate(dateTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val budget = dao.getBudgetByDate(dateTime) ?: BudgetModel(dateTime = dateTime, budget = 0f)
            _budgetByDate.postValue(budget)
        }
    }

    fun updateBudgetSpentByDate(dateTime: String, newBudget : Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val budget = dao.getBudgetByDate(dateTime)
            if (budget != null) {
                val updated = budget.copy(budget = newBudget)
                dao.updateBudget(updated)
                Log.d("BudgetViewModel", "Updated budget: $updated")
                fetchAllBudget()
            }
        }
    }




}