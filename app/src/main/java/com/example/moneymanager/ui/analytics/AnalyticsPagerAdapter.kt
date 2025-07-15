package com.example.moneymanager.ui.analytics

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneymanager.ui.analytics.fragment.ExpenseAnalyticFragment
import com.example.moneymanager.ui.analytics.fragment.IncomeAnalyticFragment
import com.example.moneymanager.ui.analytics.fragment.LoanAnalyticFragment

class AnalyticsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ExpenseAnalyticFragment()
            1 -> IncomeAnalyticFragment()
            2 -> LoanAnalyticFragment()
            else -> throw IllegalArgumentException("Invalid page")
        }
    }
}
