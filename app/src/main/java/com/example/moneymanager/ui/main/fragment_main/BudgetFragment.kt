package com.example.moneymanager.ui.main.fragment_main

// HomeFragment.kt

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneymanager.R
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.dialog.MonthYearPickerDialog
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.BudgetViewModel
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

class BudgetFragment : BaseFragment<FragmentBudgetBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBudgetBinding {
        return FragmentBudgetBinding.inflate(layoutInflater)
    }

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var transactionViewModel: SaveTransactionViewModel
    private var selectedMonth = 0
    private var selectedYear = 0
    private var sortByYear = false

    override fun initView() {

        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(SaveTransactionViewModel::class.java)
        val calendar = Calendar.getInstance()
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        transactionViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)

        observeBudget()
    }

    override fun viewListener() {
        binding.imgCalendar.setOnClickListener {
            val dialog = MonthYearPickerDialog(
                requireActivity(),
                selectedYear,
                selectedMonth,
                sortByYear
            ) { year, month, sort ->
                selectedYear = year
                selectedMonth = month
                sortByYear = sort
                Log.d("MonthYear", "Selected: $month/$year sort by year: $sortByYear")
                transactionViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)
            }
            dialog.show()
        }
    }

    override fun dataObservable() {
        transactionViewModel.filteredTransactions.observe(viewLifecycleOwner) { list ->
            val spent = list
                .filter { it.type == "Expense" }
                .sumOf { it.amount.toDouble() }
                .toFloat()

            Log.d("BudgetFragment", "Tổng chi tiêu từ filteredTransactions: $spent")
            binding.tvSpent.text = "${getString(R.string.spent)} ${formatCurrency(spent.toDouble(), DataApp.getCurrency().country)}"
        }
    }

    private fun observeBudget() {
        budgetViewModel.getBudgetByDate("${selectedMonth + 1}/$selectedYear")
        binding.tvDateTime.text = "${getMonthName(selectedMonth)}, $selectedYear"
        budgetViewModel.budgetByDate.observe(viewLifecycleOwner) { budget ->
            Log.d("BudgetFragment", "Received budget: $budget")
            // Cập nhật UI ở đây
            binding.tvBudget.text = formatCurrency(budget.budget.toDouble(), DataApp.getCurrency().country)

        }
    }

    override fun onResume() {
        super.onResume()
        transactionViewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            transactionViewModel.filterTransactions(month, year, sortByYear)
        }
    }

    private fun getMonthName(month: Int): String {
        return java.text.DateFormatSymbols().months[month]
    }
}
