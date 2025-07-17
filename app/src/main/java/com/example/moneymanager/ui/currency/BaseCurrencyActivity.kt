package com.example.moneymanager.ui.currency

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityBaseCurrencyBinding

class BaseCurrencyActivity : BaseActivity<ActivityBaseCurrencyBinding>() {
    override fun setViewBinding(): ActivityBaseCurrencyBinding {
        return ActivityBaseCurrencyBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}