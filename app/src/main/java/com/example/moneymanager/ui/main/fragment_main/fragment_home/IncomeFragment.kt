package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentIncomeBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.visible

class IncomeFragment:BaseFragment<FragmentIncomeBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel
    private lateinit var adapter: ExpensesAdapter
    private var totalIncome: Float = 0f

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIncomeBinding {
        return FragmentIncomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        adapter = ExpensesAdapter(emptyList(),true)
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
            val grouped = filtered.filter { it.type =="Income"}
            totalIncome = grouped.sumOf { it.amount.toDouble() }.toFloat()
            binding.tvIncomeTotal.text = formatCurrency(totalIncome.toDouble(), DataApp.getCurrency().country)
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


    override fun onResume() {
        super.onResume()
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            viewModel.filterTransactions(month, year, sortByYear)
        }
    }

    override fun viewListener() {}

}