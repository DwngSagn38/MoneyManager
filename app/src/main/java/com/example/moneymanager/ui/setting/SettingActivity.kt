package com.example.moneymanager.ui.setting

import android.app.AlertDialog
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivitySettingBinding
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.language.LanguageActivity

import com.example.moneymanager.utils.helper.HelperMenu

import com.example.moneymanager.widget.tap
import java.io.File

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private var helperMenu: HelperMenu? = null
    private lateinit var prefs: PreferenceManager

    override fun setViewBinding(): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        prefs = PreferenceManager(this)
        helperMenu = HelperMenu(this)
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

        }
    }

//    private fun checkSwitch() {
//        val thumbStates = ContextCompat.getColorStateList(this, R.color.switch_thumb_color)
//        val trackStates = ContextCompat.getColorStateList(this, R.color.switch_track_color)
//        binding.swSound.thumbTintList = thumbStates
//        binding.swSound.trackTintList = trackStates
//
//    }

    override fun dataObservable() {
    }
}