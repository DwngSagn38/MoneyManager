package com.example.moneymanager.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneymanager.ui.main.fragment_main.fragment_home.ExpensesFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.IncomeFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.LoansFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = listOf(
        ExpensesFragment(),
        IncomeFragment(),
        LoansFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
