package com.example.moneymanager.ui.set_your_budget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivitySetYourBudgetBinding
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.main.MainActivity
import com.example.moneymanager.utils.extensions.formatDate
import com.example.moneymanager.viewmodel.BudgetViewModel
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import java.util.Calendar

class SetYourBudgetActivity : BaseActivity<ActivitySetYourBudgetBinding>() {
    override fun setViewBinding(): ActivitySetYourBudgetBinding {
        return ActivitySetYourBudgetBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: BudgetViewModel
    private lateinit var pref : PreferenceManager
    override fun initView() {
        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
        pref = PreferenceManager(this)
    }

    override fun viewListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnSave.setOnClickListener {
            val budget = binding.edtBudget.text.toString()
            if (budget.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_enter_your_budget), Toast.LENGTH_SHORT).show()
            }else{
                val calendar = Calendar.getInstance()
                val dateTime = "${formatDate(calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR))}"
                pref.saveYourBudget(true)
                viewModel.insertBudget(dateTime, budget.toFloat(), true, "")
                showActivity(MainActivity::class.java)
            }
        }
    }

    override fun dataObservable() {
    }

}