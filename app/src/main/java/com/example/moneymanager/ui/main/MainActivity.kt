package com.example.moneymanager.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity2
import com.example.moneymanager.databinding.ActivityMainBinding
import com.example.moneymanager.ui.expense.AddTransactionActivity
import com.example.moneymanager.ui.main.fragment_main.AnalyticsFragment
import com.example.moneymanager.ui.main.fragment_main.BudgetFragment
import com.example.moneymanager.ui.main.fragment_main.HomeFragment
import com.example.moneymanager.ui.main.fragment_main.TransactionFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.visible
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : BaseActivity2<ActivityMainBinding>() {

    override fun setViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        openFragment(HomeFragment())
    }
    private lateinit var viewModel: SaveTransactionViewModel

    override fun viewListener() {
        viewModel = ViewModelProvider(this).get(SaveTransactionViewModel::class.java)

        binding.clHome.setOnClickListener {
            openFragment(HomeFragment())
            setColorDefault().also { binding.imgHome.setColorFilter(Color.parseColor("#FFFFFF")) }
            setVisibilityDot().also { binding.icDotHome.visible() }
        }
        binding.clAnalytics.setOnClickListener {
            openFragment(AnalyticsFragment())
            setColorDefault().also { binding.imgAnalytics.setColorFilter(Color.parseColor("#FFFFFF")) }
            setVisibilityDot().also { binding.icDotAnalytics.visible() }
        }
        binding.clBudget.setOnClickListener {
            openFragment(BudgetFragment())
            setColorDefault().also { binding.imgBudget.setColorFilter(Color.parseColor("#FFFFFF")) }
            setVisibilityDot().also { binding.icDotBudget.visible() }
        }
        binding.clTransaction.setOnClickListener {
            openFragment(TransactionFragment())
            setColorDefault().also { binding.imgTransaction.setColorFilter(Color.parseColor("#FFFFFF")) }
            setVisibilityDot().also { binding.icDotTransaction.visible() }
        }
        binding.icAddTransaction.setOnClickListener {
            showAddTransactionBottomSheet()
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    private fun showAddTransactionBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_transaction, null)
        dialog.setContentView(view)

// 💥 Xóa nền trắng từ FrameLayout cha của BottomSheet
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        dialog.show()


        val tvAddExpense = view.findViewById<TextView>(R.id.tv_add_expense)
        val tvAddIncome = view.findViewById<TextView>(R.id.tv_add_income)
        val tvAddLoans = view.findViewById<TextView>(R.id.tv_add_loans)
        val ivClose = view.findViewById<ImageView>(R.id.ic_close)

        tvAddExpense.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("EXTRA_TYPE", 1)
            }
            showActivity(AddTransactionActivity::class.java,bundle)
            dialog.dismiss()
        }
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        tvAddIncome.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("EXTRA_TYPE", 2)
            }
            showActivity(AddTransactionActivity::class.java,bundle)
            dialog.dismiss()
        }
        tvAddLoans.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("EXTRA_TYPE", 3)
            }
            showActivity(AddTransactionActivity::class.java,bundle)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setColorDefault() {
        binding.imgHome.setColorFilter(Color.parseColor("#6E97FF"))
        binding.imgAnalytics.setColorFilter(Color.parseColor("#6E97FF"))
        binding.imgBudget.setColorFilter(Color.parseColor("#6E97FF"))
        binding.imgTransaction.setColorFilter(Color.parseColor("#6E97FF"))
    }

    private fun setVisibilityDot() {
        binding.icDotHome.visibility = View.GONE
        binding.icDotAnalytics.visibility = View.GONE
        binding.icDotBudget.visibility = View.GONE
        binding.icDotTransaction.visibility = View.GONE
    }


    override fun dataObservable() {}
}
