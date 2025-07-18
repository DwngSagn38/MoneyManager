package com.example.moneymanager.ui.currency

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivityBaseCurrencyBinding
import com.example.moneymanager.model.CurrencyModel
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.main.MainActivity
import com.example.moneymanager.ui.set_your_budget.SetYourBudgetActivity
import com.example.moneymanager.widget.tap
import com.example.moneymanager.widget.visible

class BaseCurrencyActivity : BaseActivity<ActivityBaseCurrencyBinding>() {
    override fun setViewBinding(): ActivityBaseCurrencyBinding {
        return ActivityBaseCurrencyBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: CurrencyAdapter
    private lateinit var pref : PreferenceManager
    private var idCurrency = 0
    private lateinit var currency : CurrencyModel

    override fun initView() {
        pref = PreferenceManager(this)
        setData()
    }

    override fun viewListener() {
        binding.ivBack.tap { finish() }
        binding.ivDone.tap {
            pref.saveCurrency(idCurrency)
            pref.saveCheckCurrency(true)
            DataApp.setCurrency(currency)
            val checkBudget = pref.getYourBudget()
            if (checkBudget){
                showActivity(MainActivity::class.java)
            }else{
                showActivity(SetYourBudgetActivity::class.java)
            }
        }
    }

    override fun dataObservable() {
    }

    private fun setData(){
        adapter = CurrencyAdapter{
            binding.ivDone.visible()
            idCurrency = it.id
            currency = it
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        adapter.addList(DataApp.getListCurrency().toMutableList())

    }

}