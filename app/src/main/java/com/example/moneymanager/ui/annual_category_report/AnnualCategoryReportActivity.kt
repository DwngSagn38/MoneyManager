package com.example.moneymanager.ui.annual_category_report

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityAnnualCategoryReportBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.utils.helper.AnalyticsChartHelper.setupBarChartFor12Months
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
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
        }
    }


    override fun viewListener() {
        // Thêm xử lý sự kiện nếu cần
    }

    override fun dataObservable() {
        // Thêm quan sát dữ liệu nếu cần
    }
}
