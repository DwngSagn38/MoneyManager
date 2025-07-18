package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.helper.AnalyticsChartHelper
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.visible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesFragment : BaseFragment<FragmentExpensesBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel
    private lateinit var adapter: ExpensesAdapter
    private var totalExpenses: Float = 0f

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExpensesBinding {
        return FragmentExpensesBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        adapter = ExpensesAdapter(emptyList(), false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity()).get(SaveTransactionViewModel::class.java)
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            Log.d(
                "ExpenseAnalyticFragment",
                "selectedDate changed: $month/$year, sortByYear=$sortByYear"
            )
            viewModel.filterTransactions(month, year, sortByYear)
        }
    }

    override fun dataObservable() {
        viewModel.filteredTransactions.observe(viewLifecycleOwner) { filtered ->
            val grouped = filtered.filter { it.type == "Expense" }
            totalExpenses = grouped.sumOf { it.amount.toDouble() }.toFloat()
            binding.tvExpensesTotal.text = formatCurrency(totalExpenses.toDouble(), DataApp.getCurrency().country)
            val groupedList = groupTransactionsByDate(grouped)
            if (groupedList.size != 0) {
                binding.recyclerView.visible()
                binding.clNull.gone()
            } else {
                binding.recyclerView.gone ()
                binding.clNull.visible()
            }
            adapter.submitList(groupedList)
            Log.d("ExpenseAnalyticFragment", "Filtered Transactions: $grouped")
        }
    }


    private fun groupTransactionsByDate(transactions: List<TransactionEntity>): List<ExpenseListItem> {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse chuỗi date đúng cách
        val groupedMap = transactions.sortedByDescending {
            inputFormat.parse(it.date) // sắp xếp đúng ngày
        }.groupBy {
            inputFormat.format(inputFormat.parse(it.date)!!) // group đúng ngày
        }

        val result = mutableListOf<ExpenseListItem>()
        for ((date, list) in groupedMap) {
            val total = list.sumOf {
                if (it.check) it.amount.toDouble() else -it.amount.toDouble()
            }
            result.add(ExpenseListItem.DateHeader(date, total))
            result.addAll(list.map { ExpenseListItem.ExpenseItem(it) })
        }
        return result
    }


    override fun onResume() {
        super.onResume()
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            viewModel.filterTransactions(month, year, sortByYear)
        }
    }


    override fun viewListener() {}


}
