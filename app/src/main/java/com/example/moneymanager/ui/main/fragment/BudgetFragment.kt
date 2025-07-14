package com.example.moneymanager.ui.main.fragment

// HomeFragment.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.databinding.FragmentHomeBinding
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
