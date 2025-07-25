package com.example.moneymanager.ui.main.fragment_main

// HomeFragment.kt

import android.content.Intent
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
import com.example.moneymanager.dialog.EditBudgetBottomSheet
import com.example.moneymanager.dialog.MonthYearPickerDialog
import com.example.moneymanager.ui.budget.BudgetDetailActivity
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.extensions.formatDate
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
    private var spent = 0f
    private var budget = 0f
    private var remainingPercent  = 100f


    override fun initView() {

        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(SaveTransactionViewModel::class.java)
        val calendar = Calendar.getInstance()
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        transactionViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)

        observeBudget()

        remainingPercent = if (budget > 0f) ((budget - spent) / budget) * 100f else 100f
        binding.circleProgress.progress = remainingPercent

        // Cập nhật TextView ở ngoài
        binding.tvRemaining.text = "${String.format("%.2f", remainingPercent)}%"
        Log.d("BudgetFragment", "Remaining percent: $remainingPercent")


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
                Log.d("MonthYear", "Selected: ${formatDate(selectedMonth,selectedYear)} sort by year: $sortByYear")
                binding.tvDateTime.text = "${getMonthName(selectedMonth)}, $selectedYear"
                transactionViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)
                budgetViewModel.getBudgetByDate(formatDate(selectedMonth,selectedYear), true)
            }
            dialog.show()
        }

        binding.imgEditBudget.setOnClickListener {
            val dialog = EditBudgetBottomSheet(budget) { newBudget ->
                // Cập nhật giao diện khi ngân sách thay đổi
                binding.tvBudget.text = formatCurrency(newBudget.toDouble(),DataApp.getCurrency().country)
                budgetViewModel.updateBudgetSpentByDate( formatDate(selectedMonth,selectedYear), newBudget)
                transactionViewModel.filterTransactions(selectedMonth, selectedYear, sortByYear)
            }
            dialog.show(parentFragmentManager, "EditBudgetDialog")
        }

        binding.btnBudgetDetail.setOnClickListener {
            val intent = Intent(requireContext(), BudgetDetailActivity::class.java)
            intent.putExtra("date", formatDate(selectedMonth,selectedYear))
            intent.putExtra("spent", spent)
            intent.putExtra("budget", budget)
            startActivity(intent)
        }

    }

    override fun dataObservable() {
        transactionViewModel.filteredTransactions.observe(viewLifecycleOwner) { list ->
            spent = list
                .filter { it.type == "Expense" }
                .sumOf { it.amount.toDouble() }
                .toFloat()

            binding.tvSpent.text = "${getString(R.string.spent)} ${formatCurrency(spent.toDouble(), DataApp.getCurrency().country)}"
            updateRemainingProgress()
        }

    }

    private fun observeBudget() {
        budgetViewModel.getBudgetByDate(formatDate(selectedMonth,selectedYear), true)
        Log.d("BudgetFragment", "Observing budget for ${selectedMonth + 1}/$selectedYear")
        binding.tvDateTime.text = "${getMonthName(selectedMonth)}, $selectedYear"
        budgetViewModel.budgetByDate.observe(viewLifecycleOwner) { bg ->
            budget = bg.budget
            Log.d("BudgetFragment", "Received budget: $budget")
            updateRemainingProgress()
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

    private fun updateRemainingProgress() {
        if (budget > 0f) {
            remainingPercent = ((budget - spent) / budget) * 100f
        } else {
            remainingPercent = 0f
        }

        // Clamp trong khoảng [0, 100]
        remainingPercent = remainingPercent.coerceIn(0f, 100f)
        binding.tvBudget.text = formatCurrency(budget.toDouble(), DataApp.getCurrency().country)
        binding.circleProgress.progress = remainingPercent
        binding.tvRemaining.text = "${String.format("%.2f", remainingPercent)}%"
        Log.d("BudgetFragment", "Remaining percent updated: $remainingPercent")
    }

}
