package com.example.moneymanager.ui.main.fragment_main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.example.moneymanager.ui.main.adapter.HomePagerAdapter
import com.example.moneymanager.ui.main.fragment_main.fragment_home.ExpensesFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.IncomeFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.LoansFragment
import com.example.moneymanager.view.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                highlightTab(position)
            }
        })
        highlightTab(0)
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
    }

    private fun highlightTab(index: Int) {
        val hovers = listOf(binding.ivExpenses, binding.ivIncome, binding.ivLoans)
        hovers.forEachIndexed { i, imageView ->
            if (i == index) imageView.setImageResource(R.drawable.hover)
            else imageView.setImageDrawable(null)
        }
    }

    override fun dataObservable() {}
}
