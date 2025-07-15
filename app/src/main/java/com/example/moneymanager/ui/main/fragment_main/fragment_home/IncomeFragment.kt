package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentIncomeBinding
import com.example.moneymanager.view.base.BaseFragment

class IncomeFragment:BaseFragment<FragmentIncomeBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIncomeBinding {
        return FragmentIncomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }
}