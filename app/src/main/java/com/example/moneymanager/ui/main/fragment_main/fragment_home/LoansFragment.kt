package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.databinding.FragmentLoansBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.visible

class LoansFragment:BaseFragment<FragmentLoansBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel
    private lateinit var adapter: ExpensesAdapter
    private var totalLoans: Float = 0f
    private var totalBorrower: Float = 0f

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoansBinding {
        return FragmentLoansBinding.inflate(layoutInflater)
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
        viewModel.loadTransactionsByType("Loans")
    }

    override fun dataObservable() {
        viewModel.filteredTransactions.observe(viewLifecycleOwner) { filtered ->
            val grouped = filtered.filter { it.type == "Loans" }
            totalLoans = grouped.filter { it.check == true }.sumOf { it.amount.toDouble() }.toFloat()
            totalBorrower = grouped.filter { it.check == false }.sumOf { it.amount.toDouble() }.toFloat()
            binding.tvLoans.text = formatCurrency(totalLoans.toDouble(), DataApp.getCurrency().country)
            binding.tvBorrowed.text = formatCurrency(totalBorrower.toDouble(), DataApp.getCurrency().country)
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
            val totalLoans = list.filter { it.check == true }.sumOf { it.amount.toDouble() }.toFloat()
            val totalborrower = list.filter { it.check == false }.sumOf { it.amount.toDouble() }.toFloat()
            val total = totalLoans - totalborrower
            result.add(ExpenseListItem.DateHeader(date, total.toDouble()))
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