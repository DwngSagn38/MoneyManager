package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.databinding.FragmentLoansBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel

class LoansFragment:BaseFragment<FragmentLoansBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel
    private lateinit var adapter: ExpensesAdapter
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

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[SaveTransactionViewModel::class.java]

        viewModel.loadTransactionsByType("Loans")
    }

    override fun dataObservable() {
        lifecycleScope.launchWhenStarted {
            viewModel.loans.collect { transactions ->
                val groupedList = groupTransactionsByDate(transactions)
                adapter.submitList(groupedList)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.totalLoans.collect { total ->
                binding.tvLoans.text = "$%.2f".format(total)
            }

        }
        lifecycleScope.launchWhenStarted {
            viewModel.totalBorrowed.collect { total ->
                binding.tvBorrowed.text = "$%.2f".format(total)
            }
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
        viewModel.loadTransactionsByType("Loans")
        super.onResume()
    }
    override fun viewListener() {}

}