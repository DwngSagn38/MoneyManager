package com.example.moneymanager.utils.helper

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.custom_view.RoundedBarChartRenderer
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentExpenseAnalyticBinding
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.ui.analytics.AnalyticsDetailFragment
import com.example.moneymanager.ui.analytics.adapter.Expense1Adapter
import com.example.moneymanager.ui.analytics.adapter.ExpenseAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

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

    fun setupBarChart(context: Context, barChart: BarChart, list: List<TransactionEntity>) {
        val barEntries = list.mapIndexed { index, cat ->
            BarEntry(index.toFloat(), cat.amount.toFloat())
        }

        val barDataSet = BarDataSet(barEntries, "").apply {
            colors = list.map { it.color }
            valueTextColor = Color.TRANSPARENT
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.3f
        }

        barChart.apply {
            data = barData
            renderer = RoundedBarChartRenderer( // ✅ gán renderer SAU khi đã có data
                this,
                animator,
                viewPortHandler,
                radius = 20f
            )
            renderer.initBuffers()

            description.isEnabled = false
            axisRight.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(list.map { it.name })
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = Color.WHITE
                axisMinimum = 0f // Luôn bắt đầu từ 0
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatCurrency(value.toDouble(), DataApp.getCurrency().country)
                    }
                }
            }

            legend.isEnabled = false
            setScaleEnabled(false)           // Tắt pinch zoom (cả X và Y)
            isDoubleTapToZoomEnabled = false // Tắt double-tap để zoom
            setPinchZoom(false)
            animateY(800)
            invalidate()
        }
    }


    fun setupRecyclerView(
        context: Context,
        binding: FragmentExpenseAnalyticBinding,
        list: List<TransactionEntity>,
        fragmentManager: FragmentManager,
        month : Int,
        year : Int,
        sortByYear: Boolean
    ) {
        var displayedList = if (list.size > 6) list.sortedByDescending { it.amount.toFloat() }.take(6) else list
        val adapter = ExpenseAdapter(displayedList) { transaction ->
            // Khi item được click -> mở Fragment chi tiết
            val detailFragment = AnalyticsDetailFragment()

            // Gửi dữ liệu qua bằng Bundle (giả sử bạn muốn gửi name, amount,...)
            val bundle = Bundle().apply {
                putString("name", transaction.name)
                putInt("month", month)
                putInt("year", year)
                putBoolean("sortByYear", sortByYear)
            }
            detailFragment.arguments = bundle

            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, detailFragment) // Sửa ID theo layout của bạn
                .addToBackStack(null)
                .commit()
        }
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
        val totalAmount = list.sumOf { it.amount.toDouble() }
        val adapter = Expense1Adapter(displayedList, totalAmount.toFloat())

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

    fun setupBarChartForTwoMonths(
        context: Context,
        barChart: BarChart,
        transactions: List<TransactionEntity>,
        currentMonth: Int,
        currentYear: Int,
        sortByYear: Boolean
    ) {
        val thisMonth = currentMonth + 1
        val lastMonth = if (thisMonth == 1) 12 else thisMonth - 1
        val lastMonthYear = if (thisMonth == 1) currentYear - 1 else currentYear

        var thisMonthTotal = 0f
        var lastMonthTotal = 0f

        for (transaction in transactions) {
            val parts = transaction.date.split("/")
            if (parts.size != 3) continue

            val itemDay = parts[0].toIntOrNull() ?: continue
            val itemMonth = parts[1].toIntOrNull() ?: continue
            val itemYear = parts[2].toIntOrNull() ?: continue

            val amount = transaction.amount

            if (!sortByYear){
                if (itemYear == currentYear && itemMonth == thisMonth) {
                    thisMonthTotal += amount
                } else if (itemYear == lastMonthYear && itemMonth == lastMonth) {
                    lastMonthTotal += amount
                }
            }else{
                if (itemYear == currentYear) {
                    thisMonthTotal += amount
                } else if (itemYear == currentYear - 1) {
                    lastMonthTotal += amount
                }
            }
        }

        val entries = listOf(
            BarEntry(0f, lastMonthTotal),
            BarEntry(1f, thisMonthTotal)
        )

        val barDataSet = BarDataSet(entries, "").apply {
            colors = listOf(
                ContextCompat.getColor(context, R.color.color_6480F1),
                ContextCompat.getColor(context, R.color.main)
            )
//            color = ContextCompat.getColor(context, R.color.color_6480F1)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }


        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                val value = barEntry?.y ?: 0f
                val currencySymbol = formatCurrency(value.toDouble(), DataApp.getCurrency().country)
                return "$currencySymbol"
            }
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.4f
        }

        barChart.apply {
            data = barData
            notifyDataSetChanged() // ✅ cập nhật lại dữ liệu → khởi tạo mBarBuffers
            renderer = RoundedBarChartRenderer( // ✅ gán renderer SAU khi đã có data
                this,
                animator,
                viewPortHandler,
                radius = 20f
            )
            renderer.initBuffers()

            description.isEnabled = false
            axisRight.isEnabled = false
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(if (sortByYear) listOf("Last Year", "This Year") else listOf("Last Month", "This Month"))
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.WHITE
            }
            axisLeft.apply {
                isEnabled = true                // vẫn bật để vẽ đường $0
                axisMinimum = 0f               // giá trị thấp nhất là 0
                axisMaximum = maxOf(thisMonthTotal, lastMonthTotal) * 1.2f // tuỳ bạn
                setDrawAxisLine(false)         // không vẽ cột trục Y
                setDrawGridLines(false)        // không vẽ các đường kẻ ngang
                textColor = Color.WHITE
                textSize = 12f
                setLabelCount(1, true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value == 0f) {
                            formatCurrency(0.0, DataApp.getCurrency().country) // chỉ hiện $0
                        } else {
                            "" // các giá trị khác không hiện gì
                        }
                    }
                 }
            }
            legend.isEnabled = false
            setScaleEnabled(false)           // Tắt pinch zoom (cả X và Y)
            isDoubleTapToZoomEnabled = false // Tắt double-tap để zoom
            setPinchZoom(false)
            animateY(800)
            invalidate()
        }

    }
    fun setupBarChartFor12Months(
        context: Context,
        barChart: BarChart,
        transactions: List<TransactionEntity>,
        year: Int
    ) {
        val monthlyTotals = FloatArray(12)

        for (transaction in transactions) {
            val parts = transaction.date.split("/")
            if (parts.size != 3) continue

            val day = parts[0].toIntOrNull() ?: continue
            val month = parts[1].toIntOrNull() ?: continue
            val transYear = parts[2].toIntOrNull() ?: continue

            if (transYear == year) {
                val amount = transaction.amount
                monthlyTotals[month - 1] += amount
            }
        }

        val entries = monthlyTotals.mapIndexed { index, total ->
            BarEntry(index.toFloat(), total)
        }

        val barDataSet = BarDataSet(entries, "Monthly Total").apply {
            color = Color.parseColor("#6480F1") // ✅ Màu được yêu cầu
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.4f
        }

        val monthLabels = listOf(
            "T1", "T2", "T3", "T4", "T5", "T6",
            "T7", "T8", "T9", "T10", "T11", "T12"
        )

        barChart.apply {
            data = barData
            renderer = RoundedBarChartRenderer( // ✅ gán renderer SAU khi đã có data
                this,
                animator,
                viewPortHandler,
                radius = 10f
            )
            renderer.initBuffers()
            description.isEnabled = false
            axisRight.isEnabled = false
            val typeface = ResourcesCompat.getFont(context, R.font.opensans_600)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(monthLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.WHITE
                textSize = 10f
                this.typeface = typeface  // 👈 gán font tại đây
            }
            axisLeft.apply {
                isEnabled = true
                axisMinimum = 0f
                textColor = Color.WHITE
            }
            legend.isEnabled = false
            animateY(1000)
            invalidate()
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
                    amount = group.sumOf { it.amount.toDouble() }.toFloat(),
                    type = first.type,
                    idCategory = first.idCategory,
                    color = first.color,
                    check = first.check
                )
            }
    }

}
