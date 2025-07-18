package com.example.moneymanager.ui.setting

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivitySettingBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.model.CurrencyModel
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.currency.BaseCurrencyActivity
import com.example.moneymanager.ui.expense.CategoryAdapter
import com.example.moneymanager.ui.expense.SaveTransactionActivity
import com.example.moneymanager.ui.language.LanguageActivity

import com.example.moneymanager.utils.helper.HelperMenu

import com.example.moneymanager.widget.tap
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private var helperMenu: HelperMenu? = null
    lateinit var fullList: List<Category>
    lateinit var adapter: CategoryAdapter
    private lateinit var currency : CurrencyModel

    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val type = intent.getIntExtra("EXTRA_TYPE", -1)
        fullList = getCatagoryList(type)
        helperMenu = HelperMenu(this)
        currency = DataApp.getCurrency()

//        prefs.apply {
//            binding.swSound.isChecked = getCheckSound()
//        }
//        checkSwitch()
    }

    override fun viewListener() {
//        binding.swSound.setOnCheckedChangeListener { _, isChecked ->
//            prefs.saveCheckSound(isChecked)
//        }
        binding.apply {
            ivBack.tap { finish() }
            clRateUs.tap { helperMenu?.showDialogRate(false) }
            clShare.tap { helperMenu?.showShareApp() }
            clPolicy.tap { helperMenu?.showPolicy() }
            clLanguage.tap { showActivity(LanguageActivity::class.java) }
            clFeedback.tap { helperMenu?.showDialogFeedback() }
            clAnnualCategory.tap {
                showAddTransactionBottomSheet()
            }
            clCurrency.tap {
                val intent = Intent(this@SettingActivity, BaseCurrencyActivity::class.java)
                intent.putExtra("idCurrency", currency.id)
                startActivity(intent)
            }

            tvCurrencyCurrent.text = currency.country
        }
    }

//    private fun checkSwitch() {
//        val thumbStates = ContextCompat.getColorStateList(this, R.color.switch_thumb_color)
//        val trackStates = ContextCompat.getColorStateList(this, R.color.switch_track_color)
//        binding.swSound.thumbTintList = thumbStates
//        binding.swSound.trackTintList = trackStates
//
//    }

    private fun showAddTransactionBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_choose_a_category, null)
        dialog.setContentView(view)

        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        val ivClose = view.findViewById<ImageView>(R.id.ic_close)
        val recyclerViewCategory = view.findViewById<RecyclerView>(R.id.recyclerViewCategory)

        adapter = CategoryAdapter(fullList) { category ->
            val intent = Intent(this, SaveTransactionActivity::class.java)
            intent.putExtra("CATEGORY_DATA", category)
            startActivity(intent)
        }
        recyclerViewCategory.layoutManager = GridLayoutManager(this, 4)
        recyclerViewCategory.adapter = adapter
        dialog.show()
    }


    override fun dataObservable() {
    }
}