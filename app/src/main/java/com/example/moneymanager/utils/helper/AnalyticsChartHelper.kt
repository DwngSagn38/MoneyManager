package com.example.moneymanager.utils.helper

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentExpenseAnalyticBinding
import com.example.moneymanager.model.ExpenseCategory
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.analytics.Expense1Adapter
import com.example.moneymanager.ui.analytics.ExpenseAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

object AnalyticsChartHelper {

    fun setupPieChart(context: Context, binding: FragmentExpenseAnalyticBinding, list: List<TransactionEntity>) {
        val pieEntries = list.map { PieEntry(it.amount.toFloat(), it.name) }
        val pieDataSet = PieDataSet(pieEntries, "").apply {
            colors = list.map { it.color }
            valueTextColor = Color.TRANSPARENT
            valueTextSize = 0f
            sliceSpace = 0f
        }

        val pieData = PieData(pieDataSet)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            setDrawEntryLabels(false)
            isRotationEnabled = false
            isDrawHoleEnabled = false
            setUsePercentValues(false)
            legend.isEnabled = false
            setTransparentCircleRadius(0f)
            setExtraOffsets(5f, 5f, 5f, 5f)
            animateY(1000)
            setDrawRoundedSlices(false)
            setHoleColor(Color.TRANSPARENT)
            setBackgroundColor(Color.TRANSPARENT)
            invalidate()
        }
    }

    fun setupBarChart(context: Context, binding: FragmentExpenseAnalyticBinding, list: List<TransactionEntity>) {
        val barEntries = list.mapIndexed { index, cat ->
            BarEntry(index.toFloat(), cat.amount.toFloat())
        }
        val barDataSet = BarDataSet(barEntries, "").apply {
            colors = list.map { it.color }
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.3f
            setValueTextColor(Color.WHITE)
        }

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(list.map { it.name })
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                granularity = 1f
                setDrawGridLines(false)
            }
            axisLeft.textColor = Color.WHITE
            legend.isEnabled = false
            animateY(800)
            invalidate()
        }
    }

    fun setupRecyclerView(
        context: Context,
        binding: FragmentExpenseAnalyticBinding,
        list: List<TransactionEntity>
    ) {
        var displayedList = if (list.size > 6) list.sortedByDescending { it.amount.toFloat() }.take(6) else list
        val adapter = ExpenseAdapter(displayedList)

        binding.rvExpenses.layoutManager = LinearLayoutManager(context)
        binding.rvExpenses.adapter = adapter
        binding.tvShowMore.visibility = if (list.size > 6) View.VISIBLE else View.GONE

        binding.tvShowMore.setOnClickListener {
            val isExpanded = adapter.itemCount > 6
            if (isExpanded) {
                adapter.updateData(displayedList)
                binding.tvShowMore.text = context.getString(R.string.show_more)
            } else {
                adapter.updateData(list.sortedByDescending { it.amount.toFloat() })
                binding.tvShowMore.text = context.getString(R.string.show_less)
            }
        }
    }

    fun setupRecyclerView1(
        context: Context,
        binding: FragmentExpenseAnalyticBinding,
        list: List<TransactionEntity>
    ) {
        var displayedList = if (list.size > 6) list.take(6) else list
        val adapter = Expense1Adapter(displayedList)

        binding.rvExpenses1.layoutManager = GridLayoutManager(context, 2)
        binding.rvExpenses1.adapter = adapter
        binding.tvShowMore1.visibility = if (list.size > 6) View.VISIBLE else View.GONE

        binding.tvShowMore1.setOnClickListener {
            val isExpanded = adapter.itemCount > 6
            if (isExpanded) {
                adapter.updateData(list.take(6))
                binding.tvShowMore1.text = context.getString(R.string.show_more)
            } else {
                adapter.updateData(list)
                binding.tvShowMore1.text = context.getString(R.string.show_less)
            }
        }
    }

    fun groupByTransaction(list: List<TransactionEntity>): List<TransactionEntity> {
        return list
            .groupBy { it.name }
            .map { (name, group) ->
                val first = group.first()
                TransactionEntity(
//                    id = 0, // Bạn có thể để mặc định hoặc tự set nếu cần
                    img = first.img,
                    name = name,
                    note = first.note, // Hoặc kết hợp các ghi chú nếu muốn
                    time = first.time,
                    date = group.minByOrNull { it.date }?.date ?: first.date, // ngày nhỏ nhất
                    amount = group.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }.toString(),
                    type = first.type,
                    idCategory = first.idCategory,
                    color = first.color
                )
            }
    }

}
