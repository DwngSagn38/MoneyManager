package com.example.moneymanager.ui.analytics.fragment

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class ExpenseAnalyticFragment : BaseFragment<FragmentExpenseAnalyticBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentExpenseAnalyticBinding {
        return FragmentExpenseAnalyticBinding.inflate(inflater, container, false)
    }

    private lateinit var sharedViewModel: SharedAnalyticsViewModel

    override fun initView() {
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedAnalyticsViewModel::class.java)
        sharedViewModel.selectedDate.observe(viewLifecycleOwner) { (month, year, sortByYear) ->
            sharedViewModel.filterCategories(month, year, sortByYear)

            if (sortByYear) {
                binding.tvTimeCheck.text = "$year"
                binding.tvTimeCheck2.text = "$year"
            } else {
                binding.tvTimeCheck.text = "${getMonthName(month)}, $year"
                binding.tvTimeCheck2.text = "${getMonthName(month)}, $year"
            }
        }


         // Cập nhật RecyclerView khi danh sách thay đổi
        sharedViewModel.filteredCategories.observe(viewLifecycleOwner) { filtered ->
            val grouped = AnalyticsChartHelper.groupByCategory(filtered)

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

}