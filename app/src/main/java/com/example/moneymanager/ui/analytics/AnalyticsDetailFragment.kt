package com.example.moneymanager.ui.analytics

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentAnalyticsDetailBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpenseListItem
import com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter.ExpensesAdapter
import com.example.moneymanager.utils.helper.AnalyticsChartHelper
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel


class AnalyticsDetailFragment : BaseFragment<FragmentAnalyticsDetailBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAnalyticsDetailBinding {
        return FragmentAnalyticsDetailBinding.inflate(inflater, container, false)
    }

    private lateinit var viewModel: SaveTransactionViewModel
    private var name: String? = null
    private lateinit var adapter: ExpensesAdapter

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity()).get(SaveTransactionViewModel::class.java)
        adapter = ExpensesAdapter(emptyList(),true)
        setRecycleView()

        name = arguments?.getString("name")
        val month = arguments?.getInt("month")!!
        val year = arguments?.getInt("year")!!
        val sortByYear = arguments?.getBoolean("sortByYear") ?: false

        viewModel.filterTransactionsForTwoMonths(month, year, name ?: "", sortByYear)

        viewModel.twoMonthsTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("AnalyticsDetailFragment", "Filtered transactions: $transactions")

            AnalyticsChartHelper.setupBarChartForTwoMonths(
                requireContext(),
                binding.barChart,
                transactions.sortedByDescending { it.date },
                month,
                year,
                sortByYear
            )

            val groupedList = groupTransactionsByDate(transactions)
            adapter.submitList(groupedList)
        }

        binding.tvName.text = name
        binding.tvTotal.text = getString(R.string.total) + " " + name

    }


    override fun viewListener() {
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun dataObservable() {
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            viewModel.filterTransactions(month, year, sortByYear)
            viewModel.filterTransactionsForTwoMonths(month, year, name ?: "", sortByYear)
        }
    }

    private fun setRecycleView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        viewModel.loadTransactionsByType("Expense")
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