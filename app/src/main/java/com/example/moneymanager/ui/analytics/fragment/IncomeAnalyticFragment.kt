package com.example.moneymanager.ui.analytics.fragment

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentExpenseAnalyticBinding
import com.example.moneymanager.model.ExpenseCategory
import com.example.moneymanager.ui.analytics.Expense1Adapter
import com.example.moneymanager.ui.analytics.ExpenseAdapter
import com.example.moneymanager.ui.analytics.SharedAnalyticsViewModel
import com.example.moneymanager.utils.helper.AnalyticsChartHelper
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class IncomeAnalyticFragment: BaseFragment<FragmentExpenseAnalyticBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentExpenseAnalyticBinding {
        return FragmentExpenseAnalyticBinding.inflate(inflater, container, false)
    }

    private lateinit var viewModel: SaveTransactionViewModel

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity()).get(SaveTransactionViewModel::class.java)
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            Log.d("ExpenseAnalyticFragment", "date selected: $month, $year, $sortByYear")

            viewModel.filterTransactions(month, year, sortByYear)
            if (sortByYear) {
                binding.tvTimeCheck.text = "$year"
                binding.tvTimeCheck2.text = "$year"
            } else {
                binding.tvTimeCheck.text = "${getMonthName(month)}, $year"
                binding.tvTimeCheck2.text = "${getMonthName(month)}, $year"
            }
        }

        viewModel.filteredTransactions.observe(viewLifecycleOwner) { filtered ->
            val grouped = AnalyticsChartHelper.groupByTransaction(filtered.filter { it.type == "Income" })
            Log.d("ExpenseAnalyticFragment", "Filtered Transactions: $filtered")

            AnalyticsChartHelper.setupPieChart(requireContext(), binding, grouped)
            AnalyticsChartHelper.setupBarChart(requireContext(), binding, grouped)
            AnalyticsChartHelper.setupRecyclerView(requireContext(), binding, grouped)
            AnalyticsChartHelper.setupRecyclerView1(requireContext(), binding, grouped)
        }


    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }
    private fun getMonthName(month: Int): String {
        return java.text.DateFormatSymbols().months[month]
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            viewModel.filterTransactions(month, year, sortByYear)
        }
    }

}