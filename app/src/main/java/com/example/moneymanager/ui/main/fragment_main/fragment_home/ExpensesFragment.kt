package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel

class ExpensesFragment : BaseFragment<FragmentExpensesBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel
    private lateinit var adapter: ExpensesAdapter

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExpensesBinding {
        return FragmentExpensesBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        adapter = ExpensesAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[SaveTransactionViewModel::class.java]

        viewModel.loadTransactionsByType("Expense")
    }

    override fun dataObservable() {
        lifecycleScope.launchWhenStarted {
            viewModel.expenses.collect { transactions ->
                val groupedList = groupTransactionsByDate(transactions)
                adapter.submitList(groupedList)
            }
        }

        // 2. Observe tổng chi tiêu để hiển thị lên TextView
        lifecycleScope.launchWhenStarted {
            viewModel.totalExpenses.collect { total ->
                binding.tvExpensesTotal.text = "$%.2f".format(total)
            }
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
        viewModel.loadTransactionsByType("Expense")
        super.onResume()
    }
    override fun viewListener() {}


}
