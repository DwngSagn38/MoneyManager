package com.example.moneymanager.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityMainBinding
import com.example.moneymanager.ui.main.fragment.AnalyticsFragment
import com.example.moneymanager.ui.main.fragment.BudgetFragment
import com.example.moneymanager.ui.main.fragment.HomeFragment
import com.example.moneymanager.ui.main.fragment.TransactionFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        // Mặc định mở Home
        openFragment(HomeFragment())
    }

    override fun viewListener() {
        binding.clHome.setOnClickListener {
            openFragment(HomeFragment())
        }
        binding.clAnalytics.setOnClickListener {
            openFragment(AnalyticsFragment())
        }
        binding.clBudget.setOnClickListener {
            openFragment(BudgetFragment())
        }
        binding.clTransaction.setOnClickListener {
            openFragment(TransactionFragment())
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun dataObservable() {}
}
