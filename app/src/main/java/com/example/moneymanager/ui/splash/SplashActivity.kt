package com.example.moneymanager.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moneymanager.ui.language_start.LanguageStartActivity
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ActivitySplashBinding
import com.example.moneymanager.sharePreferent.PreferenceManager
import com.example.moneymanager.ui.currency.BaseCurrencyActivity
import com.example.moneymanager.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val croutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var pref : PreferenceManager
    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        pref = PreferenceManager(this)
        setCurrency()
        croutineScope.launch {
            delay(2000)
            startIntro()
        }
    }

    override fun viewListener() {

    }

    override fun dataObservable() {

    }

    private fun startIntro() {
        showActivity(MainActivity::class.java)
        finish()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun setCurrency(){
        val idCurrency = pref.getCurrency()
        val currency = DataApp.getListCurrency()[idCurrency]
        DataApp.setCurrency(currency)
    }
    

}