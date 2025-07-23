package com.example.moneymanager.ui.annual_category_report

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivityAnnualCategoryReportBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.helper.AnalyticsChartHelper.setupBarChartFor12Months
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.tap
import kotlinx.coroutines.launch

class AnnualCategoryReportActivity : BaseActivity<ActivityAnnualCategoryReportBinding>() {
    private lateinit var viewModel: SaveTransactionViewModel

    override fun setViewBinding(): ActivityAnnualCategoryReportBinding {
        return ActivityAnnualCategoryReportBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val category = intent.getSerializableExtra("CATEGORY_DATA") as? Category
        viewModel = ViewModelProvider(this)[SaveTransactionViewModel::class.java]

        lifecycleScope.launch {
            val year = viewModel.selectedDate.value?.second ?: 2025
            val filtered = viewModel.getTransactionsByYear(year)
            val filteredByCategory = filtered.filter { it.name == category?.name }

            setupBarChartFor12Months(
                context = this@AnnualCategoryReportActivity,
                barChart = binding.barChart,
                transactions = filteredByCategory,
                year = year
            )


            val totalAmount = filteredByCategory.sumOf { it.amount.toDouble() }
            val averageAmount = if (filteredByCategory.isNotEmpty())
                totalAmount / filteredByCategory.size else 0.0

            val currencySymbol = formatCurrency(0.0, DataApp.getCurrency().country).replace("0", "").trim()

            binding.tvTotal.text = "$currencySymbol${String.format("%.2f", totalAmount)}"
            binding.tvAverage.text = "$currencySymbol${String.format("%.2f", averageAmount)}"
            val monthAmounts = (1..12).map { month ->
                val total = filteredByCategory
                    .filter {
                        val parts = it.date.split("/")
                        parts.size == 3 && parts[1].toIntOrNull() == month
                    }.sumOf { it.amount.toDouble() }

                MonthAmount(month, total)
            }
            val adapter = MonthlyAmountAdapter(monthAmounts)
            binding.recyclerMonthly.layoutManager = LinearLayoutManager(this@AnnualCategoryReportActivity)
            binding.recyclerMonthly.adapter = adapter

        }
    }


    override fun viewListener() {
        binding.ivBack.tap {
            finish()
        }    }

    override fun dataObservable() {
        // Thêm quan sát dữ liệu nếu cần
    }
}
