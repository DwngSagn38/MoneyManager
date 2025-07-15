package com.example.moneymanager.ui.main.fragment_main

// HomeFragment.kt

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentTransactionBinding
import com.example.moneymanager.view.base.BaseFragment

class TransactionFragment : BaseFragment<FragmentTransactionBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransactionBinding {
        return FragmentTransactionBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}
