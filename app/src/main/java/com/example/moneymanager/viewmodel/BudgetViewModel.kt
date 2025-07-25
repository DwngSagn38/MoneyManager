package com.example.moneymanager.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
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

    private val _allBudgetByDate = MutableLiveData<List<BudgetModel>>()
    val allBudgetByDate: LiveData<List<BudgetModel>> get() = _allBudgetByDate

    init {
        fetchAllBudget()
    }

    private fun fetchAllBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getAllBudget()
            _allBudget.postValue(all)
        }
    }

    fun insertBudget(dateTime : String , budget : Float, type : Boolean, name : String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingBudget = dao.getBudgetByName(name!!, dateTime)
            if (existingBudget != null) {
                Log.d("BudgetViewModel", "Budget with name $name already exists")
                return@launch
            }
            val budget = BudgetModel(dateTime = dateTime, budget = budget , type = type, name = name)
            Log.d("BudgetViewModel", "Inserting budget: $budget")

            dao.insertBudget(budget)
            getListBudgetByDate(dateTime)
        }
    }

    private val _budgetByDate = MutableLiveData<BudgetModel>()
    val budgetByDate: LiveData<BudgetModel> get() = _budgetByDate

    fun getBudgetByDate(dateTime: String, type : Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val budget = dao.getBudgetByDate(dateTime)
            if (budget == null) {
                val newBudget = BudgetModel(dateTime = dateTime, budget = 0f, type = type)
                dao.insertBudget(newBudget)
                fetchAllBudget()
                Log.d("BudgetViewModel", "Inserted new budget: $newBudget")
                _budgetByDate.postValue(newBudget)
            } else {
                Log.d("BudgetViewModel", "Found existing budget: $budget")
                _budgetByDate.postValue(budget)
            }
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
                _budgetByDate.postValue(updated)
            }
        }
    }

    fun updateBudgetSpentByDateAndName(name : String?, dateTime: String, newBudget : Float) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("BudgetViewModel", "Update budget with name: $name  ; dateTime: $dateTime    ; newBudget: $newBudget")
            val budget = dao.getBudgetByName(name!!, dateTime)
            Log.d("BudgetViewModel", "Find budget: $budget")
            if (budget != null) {
                val updated = budget.copy(budget = newBudget)
                dao.updateBudget(updated)
                Log.d("BudgetViewModel", "Updated budget: $updated")
                fetchAllBudget()
                _budgetByDate.postValue(updated)
                getListBudgetByDate(dateTime)
            }
        }
    }

    fun getListBudgetByDate(dateTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getListBudgetByDate(dateTime)
            _allBudgetByDate.postValue(list)
            fetchAllBudget()
        }

    }



}