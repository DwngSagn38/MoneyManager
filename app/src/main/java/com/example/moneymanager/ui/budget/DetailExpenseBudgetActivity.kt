package com.example.moneymanager.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivityDetailExpenseBudgetBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.helper.AnalyticsChartHelper
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.tap

class DetailExpenseBudgetActivity : BaseActivity<ActivityDetailExpenseBudgetBinding>() {
    override fun setViewBinding(): ActivityDetailExpenseBudgetBinding {
        return ActivityDetailExpenseBudgetBinding.inflate(layoutInflater)
    }

    private var budget = 0f
    private var date = ""
    private var name = ""
    private var color = 0
    private lateinit var transactionViewModel: SaveTransactionViewModel
    private var spent  = 0f
    private lateinit var adapter: ExpensesAdapter

    override fun initView() {
        adapter = ExpensesAdapter(emptyList(),true)

        budget = intent.getFloatExtra("budget",0f)
        color = intent.getIntExtra("color",0)
        date = intent.getStringExtra("date") ?: ""
        name = intent.getStringExtra("name") ?: ""
        spent = intent.getFloatExtra("spent",0f)
        Log.d("DetailExpense1111", "budget: $budget")
        Log.d("DetailExpense1111", "color: $color")
        Log.d("DetailExpense1111", "date: $date")
        Log.d("DetailExpense1111", "name: $name")
        Log.d("DetailExpense1111", "spent : $spent")

        transactionViewModel = ViewModelProvider(this).get(SaveTransactionViewModel::class.java)
        val selectedMonth = date.split("/")[0].toInt()
        val selectedYear = date.split("/")[1].toInt()
        transactionViewModel.filterTransactionsForTwoMonths(selectedMonth, selectedYear, name, false)

        setData()

        transactionViewModel.twoMonthsTransactions.observe(this) { transactions ->
            Log.d("DetailExpense1111", "Filtered transactions: $transactions")

            val groupedList = groupTransactionsByDate(transactions)
            adapter.submitList(groupedList)
        }



    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
    }

    override fun dataObservable() {
    }

    private fun setData(){
        var remainingPercent = if (budget > 0f) {
            ((budget - spent) / budget * 100f).coerceIn(0f, 100f)
        } else 0f


        binding.apply {
            tvTitle.text = name
            tvTotalSpent.text = formatCurrency(spent.toDouble(), DataApp.getCurrency().country)
            tvTotalBudget.text = formatCurrency(budget.toDouble(), DataApp.getCurrency().country)
            tvRemain.text = "${String.format("%.2f", remainingPercent)}%"
        }


        binding.circleProgress.apply {
            progress = remainingPercent
            animateToProgress(remainingPercent)
            startWaveAnimation()
            waveColor = if (remainingPercent == 100f) Color.parseColor("#848CFF") else color
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        transactionViewModel.loadTransactionsByType("Expense")
    }

    private fun groupTransactionsByDate(transactions: List<TransactionEntity>): List<ExpenseListItem> {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val groupedMap = transactions.sortedByDescending { it.time }
            .groupBy { sdf.format(java.util.Date(it.date)) }

        val result = mutableListOf<ExpenseListItem>()
        for ((date, list) in groupedMap) {
            val total = list.sumOf { it.amount.toDouble() }
            result.add(ExpenseListItem.DateHeader(date, total))
            result.addAll(list.map { ExpenseListItem.ExpenseItem(it) })
        }
        return result
    }
}