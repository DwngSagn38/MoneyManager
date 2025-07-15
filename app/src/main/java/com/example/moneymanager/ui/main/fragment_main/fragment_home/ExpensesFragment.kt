package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.view.base.BaseFragment

class ExpensesFragment:BaseFragment<FragmentExpensesBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExpensesBinding {
        return FragmentExpensesBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }
}