package com.example.moneymanager.ui.main.fragment_main


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentAnalyticsBinding
import com.example.moneymanager.model.ExpenseCategory
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

class AnalyticsFragment : BaseFragment<FragmentAnalyticsBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAnalyticsBinding {
        return FragmentAnalyticsBinding.inflate(inflater, container, false)
    }

    private val categories = listOf(
        ExpenseCategory("Transportation", 900f, Color.parseColor("#627BFF"), R.drawable.ic_expenses),
        ExpenseCategory("Pets", 200f, Color.parseColor("#FF6F91"), R.drawable.ic_loans),
        ExpenseCategory("Education", 160f, Color.parseColor("#46D9F3"), R.drawable.ic_income)
    )

    override fun initView() {
        val pieEntries = categories.map {
            PieEntry(it.amount, it.name)
        }
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = categories.map { it.color }
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)

        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.setDrawHoleEnabled(true)
        binding.pieChart.centerText = "July, 2025"
        binding.pieChart.setCenterTextSize(18f)
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.animateY(1000)
        binding.pieChart.invalidate()

        binding.llLegend.removeAllViews()
        categories.forEach { cat ->
            val legendView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_legend, binding.llLegend, false)
            legendView.findViewById<TextView>(R.id.tvLegendName).text = cat.name
            legendView.findViewById<TextView>(R.id.tvLegendPercent).text = "${"%.0f".format((cat.amount / 1260f) * 100)}%"
            legendView.findViewById<View>(R.id.viewColor).setBackgroundColor(cat.color)
            binding.llLegend.addView(legendView)
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

    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}