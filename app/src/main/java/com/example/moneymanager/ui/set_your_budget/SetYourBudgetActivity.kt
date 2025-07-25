package com.example.moneymanager.ui.set_your_budget

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivitySetYourBudgetBinding
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.main.MainActivity
import com.example.moneymanager.utils.extensions.formatCurrencyNotSymbol
import com.example.moneymanager.utils.extensions.formatDate
import com.example.moneymanager.utils.extensions.formatEditText
import com.example.moneymanager.viewmodel.BudgetViewModel
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.getCleanFloat
import com.example.moneymanager.widget.hideKeyboard
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
            val budget = binding.edtBudget.getCleanFloat()
            if (budget == 0f) {
                Toast.makeText(this, getString(R.string.please_enter_your_budget), Toast.LENGTH_SHORT).show()
            }else{
                val calendar = Calendar.getInstance()
                val dateTime = "${formatDate(calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR))}"
                pref.saveYourBudget(true)
                viewModel.insertBudget(dateTime, budget.toFloat(), true, "")
                showActivity(MainActivity::class.java)
            }
        }

        formatEditText(binding.edtBudget)

    }

    override fun dataObservable() {
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}