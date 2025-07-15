package com.example.moneymanager.ui.main.fragment_main.fragment_home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentExpensesBinding
import com.example.moneymanager.databinding.FragmentLoansBinding
import com.example.moneymanager.view.base.BaseFragment

class LoansFragment:BaseFragment<FragmentLoansBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoansBinding {
        return FragmentLoansBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }
}