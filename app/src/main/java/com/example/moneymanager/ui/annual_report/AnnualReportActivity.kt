package com.example.moneymanager.ui.annual_report

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivityAnnualReportBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.ui.annual_category_report.MonthAmount
import com.example.moneymanager.ui.annual_category_report.MonthlyAmountAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.helper.AnalyticsChartHelper
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.tap
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class AnnualReportActivity : BaseActivity<ActivityAnnualReportBinding>() {
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentType = "Expense"

    private lateinit var viewModel: SaveTransactionViewModel

    override fun setViewBinding(): ActivityAnnualReportBinding {
        return ActivityAnnualReportBinding.inflate(layoutInflater)
    }

    override fun initView() {
        viewModel = ViewModelProvider(this)[SaveTransactionViewModel::class.java]
        val category = intent.getSerializableExtra("CATEGORY_DATA") as? Category

        currentYear = viewModel.selectedDate.value?.second ?: 2025
        binding.tvYear.text = currentYear.toString()

        loadData(type = currentType, category = category, year = currentYear)

        binding.tvExpenses.setOnClickListener {
            currentType = "Expense"
            loadData(currentType, category, currentYear)
            binding.tvExpenses.setBackgroundResource(R.drawable.bg_748eff_radius)
            binding.tvIncome.setBackgroundResource(0) // clear background
        }

        binding.tvIncome.setOnClickListener {
            currentType = "Income"
            loadData(currentType, category, currentYear)
            binding.tvIncome.setBackgroundResource(R.drawable.bg_748eff_radius)
            binding.tvExpenses.setBackgroundResource(0)
        }
        binding.ivShare.setOnClickListener {
            val bitmap = captureFullScreen()
            shareBitmap(bitmap)
        }

    }
    private fun captureFullScreen(): Bitmap {
        val view = window.decorView.rootView
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }
    private fun shareBitmap(bitmap: Bitmap) {
        val cachePath = File(cacheDir, "images")
        cachePath.mkdirs()

        val file = File(cachePath, "shared_image.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        val contentUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


    override fun viewListener() {
        val category = intent.getSerializableExtra("CATEGORY_DATA") as? Category

        binding.ivPrevYear.setOnClickListener {
            currentYear -= 1
            binding.tvYear.text = currentYear.toString()
            loadData(currentType, category, currentYear)
        }

        binding.ivNextYear.setOnClickListener {
            currentYear += 1
            binding.tvYear.text = currentYear.toString()
            loadData(currentType, category, currentYear)
        }
        binding.ivBack.tap {
            finish()
        }
    }

    private fun loadData(type: String, category: Category?, year: Int) {
        lifecycleScope.launch {
            val allTransactions = viewModel.getTransactionsByYear(year)
            val filteredByType = allTransactions.filter { it.type == type }

            val filteredByCategory = if (category != null)
                filteredByType.filter { it.name == category.name }
            else
                filteredByType

            AnalyticsChartHelper.setupBarChartFor12Months(
                context = this@AnnualReportActivity,
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
            binding.recyclerMonthly.layoutManager = LinearLayoutManager(this@AnnualReportActivity)
            binding.recyclerMonthly.adapter = adapter
        }
    }


    override fun dataObservable() {
    }

}