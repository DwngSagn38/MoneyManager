package com.example.moneymanager.ui.main.fragment_main

// HomeFragment.kt

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.view.base.BaseFragment

class BudgetFragment : BaseFragment<FragmentBudgetBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBudgetBinding {
        return FragmentBudgetBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}
