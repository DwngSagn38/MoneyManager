package com.example.moneymanager.ui.analytics

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.moneymanager.databinding.FragmentAnalyticsDetailBinding
import com.example.moneymanager.view.base.BaseFragment


class AnalyticsDetailFragment : BaseFragment<FragmentAnalyticsDetailBinding>() {
    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAnalyticsDetailBinding {
        return FragmentAnalyticsDetailBinding.inflate(inflater, container, false)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}