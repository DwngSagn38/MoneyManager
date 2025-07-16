package com.example.moneymanager.ui.main.fragment_main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentAnalyticsBinding
import com.example.moneymanager.dialog.MonthYearPickerDialog
import com.example.moneymanager.ui.analytics.AnalyticsPagerAdapter
import com.example.moneymanager.ui.analytics.SharedAnalyticsViewModel
import com.example.moneymanager.view.base.BaseFragment
import java.util.Calendar

class AnalyticsFragment : BaseFragment<FragmentAnalyticsBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAnalyticsBinding {
        return FragmentAnalyticsBinding.inflate(inflater, container, false)
    }

    private var selectedMonth = 0
    private var selectedYear = 0
    private var sortByYear = false
    private lateinit var adapter: AnalyticsPagerAdapter
    private lateinit var sharedViewModel: SharedAnalyticsViewModel

    override fun initView() {
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedAnalyticsViewModel::class.java)
        val calendar = Calendar.getInstance()
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        sharedViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)

        adapter = AnalyticsPagerAdapter(this)
        binding.viewPager.adapter = adapter
        selectTab(0)
    }

    override fun viewListener() {
        binding.tvExpenses.setOnClickListener {
            binding.viewPager.currentItem = 0
        }
        binding.tvIncome.setOnClickListener {
            binding.viewPager.currentItem = 1
        }
        binding.tvLoans.setOnClickListener {
            binding.viewPager.currentItem = 2
        }

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectTab(position)
            }
        })

        binding.imgCalendar.setOnClickListener {
            val dialog = MonthYearPickerDialog(
                requireActivity(),
                selectedYear,
                selectedMonth,
                sortByYear
            ) { year, month, sort ->
                selectedYear = year
                selectedMonth = month
                sortByYear = sort
                Log.d("MonthYear", "Selected: $month/$year sort by year: $sortByYear")
                sharedViewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)

            }
            dialog.show()
        }
    }

    private fun selectTab(index: Int) {
        val context = requireContext()

        // Reset về màu ban đầu
        binding.tvExpenses.setTextColor(context.getColor(R.color.color_B1C7FF))
        binding.tvIncome.setTextColor(context.getColor(R.color.color_B1C7FF))
        binding.tvLoans.setTextColor(context.getColor(R.color.color_B1C7FF))

        // Highlight tab được chọn
        when (index) {
            0 -> binding.tvExpenses.setTextColor(context.getColor(R.color.white))
            1 -> binding.tvIncome.setTextColor(context.getColor(R.color.white))
            2 -> binding.tvLoans.setTextColor(context.getColor(R.color.white))
        }

        val hovers = listOf(binding.ivExpenses, binding.ivIncome, binding.ivLoans)
        hovers.forEachIndexed { i, imageView ->
            if (i == index) imageView.setImageResource(R.drawable.hover)
            else imageView.setImageDrawable(null)
        }
    }
    override fun dataObservable() {
    }

}