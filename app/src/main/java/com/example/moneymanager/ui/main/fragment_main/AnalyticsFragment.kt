package com.example.moneymanager.ui.main.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentAnalyticsBinding
import com.example.moneymanager.ui.analytics.AnalyticsPagerAdapter
import com.example.moneymanager.view.base.BaseFragment

class AnalyticsFragment : BaseFragment<FragmentAnalyticsBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAnalyticsBinding {
        return FragmentAnalyticsBinding.inflate(inflater, container, false)
    }

    private lateinit var adapter: AnalyticsPagerAdapter

    override fun initView() {
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
    }
    override fun dataObservable() {
    }

}