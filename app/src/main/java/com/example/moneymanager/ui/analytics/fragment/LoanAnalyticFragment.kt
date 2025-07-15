package com.example.moneymanager.ui.analytics.fragment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentExpenseAnalyticBinding
import com.example.moneymanager.model.ExpenseCategory
import com.example.moneymanager.ui.analytics.Expense1Adapter
import com.example.moneymanager.ui.analytics.ExpenseAdapter
import com.example.moneymanager.view.base.BaseFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class LoanAnalyticFragment: BaseFragment<FragmentExpenseAnalyticBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentExpenseAnalyticBinding {
        return FragmentExpenseAnalyticBinding.inflate(inflater, container, false)
    }

    private val categories = listOf(
        ExpenseCategory("Transportation", 300f, Color.parseColor("#627BFF"), R.drawable.ic_expenses),
        ExpenseCategory("Pets", 1460f, Color.parseColor("#FF6F91"), R.drawable.ic_loans),
        ExpenseCategory("Education", 960f, Color.parseColor("#46D9F3"), R.drawable.ic_income)
    )

    override fun initView() {
        binding.tvTimeCheck.text = "July, 2025"
        binding.tvTimeCheck2.text = "July, 2025"
        val pieEntries = categories.map {
            PieEntry(it.amount, it.name)
        }
        val pieDataSet = PieDataSet(pieEntries, "").apply {
            colors = categories.map { it.color }
            valueTextColor = Color.TRANSPARENT // Ẩn số
            valueTextSize = 0f
            sliceSpace = 0f // Không có khoảng cách giữa các phần
        }

        val pieData = PieData(pieDataSet)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            setDrawEntryLabels(false) // Ẩn nhãn trong biểu đồ
            isRotationEnabled = false // Không cho xoay
            isDrawHoleEnabled = false // Không có lỗ ở giữa
            setUsePercentValues(false)
            legend.isEnabled = false
            setTransparentCircleRadius(0f)
            setExtraOffsets(5f, 5f, 5f, 5f)
            animateY(1000)

            // Viền xám bao quanh toàn bộ hình tròn
            setDrawRoundedSlices(false)
            setHoleColor(Color.TRANSPARENT)
            setBackgroundColor(Color.TRANSPARENT)

            invalidate()
        }

        val barEntries = categories.mapIndexed { index, cat ->
            BarEntry(index.toFloat(), cat.amount)
        }
        val barDataSet = BarDataSet(barEntries, "")
        barDataSet.colors = categories.map { it.color }

        val barData = BarData(barDataSet)
        barData.barWidth = 0.3f
        barData.setValueTextColor(Color.WHITE)

        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(categories.map { it.name })
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.WHITE
            granularity = 1f
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.textColor = Color.WHITE
        binding.barChart.legend.isEnabled = false
        binding.barChart.animateY(800)
        binding.barChart.invalidate()

        // Gắn Adapter vào RecyclerView
        val adapter = ExpenseAdapter(categories)
        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        val adapter1 = Expense1Adapter(categories)
        binding.rvExpenses1.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvExpenses1.adapter = adapter1
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}