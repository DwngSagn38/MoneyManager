package com.example.moneymanager.ui.budget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivityBudgetDetailBinding
import com.example.moneymanager.dialog.AddBudgetBottomSheet
import com.example.moneymanager.dialog.EditBudgetBottomSheet
import com.example.moneymanager.model.BudgetModel
import com.example.moneymanager.model.Category
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.extensions.formatDate
import com.example.moneymanager.viewmodel.BudgetViewModel
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.invisible
import com.example.moneymanager.widget.tap
import com.example.moneymanager.widget.visible

class BudgetDetailActivity : BaseActivity<ActivityBudgetDetailBinding>() {
    override fun setViewBinding(): ActivityBudgetDetailBinding {
        return ActivityBudgetDetailBinding.inflate(layoutInflater)
    }

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var transactionViewModel: SaveTransactionViewModel
    private var spent = 0f
    private var budget = 0f
    private var date = ""
    private var remainingPercent  = 100f
    lateinit var fullList: List<Category>
    private lateinit var adapter: BudgetAdapter

    override fun initView() {
        fullList = getCatagoryList(1)
        date = intent.getStringExtra("date") ?: ""
        spent = intent.getFloatExtra("spent", 0f)
        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(SaveTransactionViewModel::class.java)
        val selectedMonth = date.split("/")[0].toInt()
        val selectedYear = date.split("/")[1].toInt()
        transactionViewModel.filterTransactions(selectedMonth, selectedYear, true)
        observeBudget(date)
        setRecyclerView(emptyList())
    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
        binding.btnAddBudget.tap {
            val dialog = AddBudgetBottomSheet(
                this@BudgetDetailActivity,
                fullList
            ) { budget, budgetName ->
                budgetViewModel.insertBudget(date,budget,false, budgetName)
                observeBudget(date)

            }
            dialog.show(supportFragmentManager,"AddBudgetDialog")
        }
    }

    override fun dataObservable() {

    }

    private fun observeBudget(date : String) {
        budgetViewModel.getBudgetByDate(date, true)
        budgetViewModel.getListBudgetByDate(date)
        budgetViewModel.budgetByDate.observe(this) { bg ->
            budget = bg.budget
            Log.d("BudgetFragment", "Received budget: $budget")
            updateRemainingProgress(bg.budget)
        }
        transactionViewModel.filteredTransactions.observe(this) { list ->
            if (!list.isEmpty()){
                binding.recyclerView.visible()
                setRecyclerView(list)
            }else{
                binding.recyclerView.invisible()
            }
            Log.d("BudgetDetail123", "list : $list")
        }

    }

    private fun updateRemainingProgress(bg : Float) {
        if (bg > 0f) {
            remainingPercent = ((bg - spent) / bg) * 100f
        } else {
            remainingPercent = 0f
        }

        // Clamp trong khoảng [0, 100]
        remainingPercent = remainingPercent.coerceIn(0f, 100f)
        binding.tvTotalBudget.text = formatCurrency(bg.toDouble(), DataApp.getCurrency().country)
        binding.tvTotalSpent.text = formatCurrency(spent.toDouble(), DataApp.getCurrency().country)
        binding.tvTotalRemaining.text = formatCurrency(bg.toDouble() - spent, DataApp.getCurrency().country)
        binding.circleProgress.progress = remainingPercent
        binding.tvRemaining.text = "${String.format("%.2f", remainingPercent)}%"
        Log.d("BudgetFragment", "Remaining percent updated: $remainingPercent")
    }

    private fun setRecyclerView(listExpense: List<TransactionEntity>) {
        Log.d("BudgetDetail123", "listExpense : $listExpense")
        adapter = BudgetAdapter(listExpense){ budget, spent, color ->
            val intent = Intent(this, DetailExpenseBudgetActivity::class.java)
            intent.putExtra("budget", budget.budget)
            intent.putExtra("name", budget.name)
            intent.putExtra("date", date)
            intent.putExtra("color", color)
            intent.putExtra("spent", spent)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        budgetViewModel.allBudgetByDate.observe(this) {
            adapter.addList(it.toMutableList())
        }
    }



}