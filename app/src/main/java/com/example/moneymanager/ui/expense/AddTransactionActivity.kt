package com.example.moneymanager.ui.expense

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivityAddTransactionBinding

class AddTransactionActivity : BaseActivity<ActivityAddTransactionBinding>() {
    override fun setViewBinding(): ActivityAddTransactionBinding {
        return ActivityAddTransactionBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun viewListener() {
    }

    override fun dataObservable() {
    }

}