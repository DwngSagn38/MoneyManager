package com.example.moneymanager.sharePreferent

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


class PreferenceManager(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "ghost_detector_prefs"
        private const val KEY_CURRENCY = "KEY_CURRENCY"
        private const val KEY_CHECK_CURRENCY = "KEY_CHECK_CURRENCY"
        private const val KEY_YOUR_BUDGET = "KEY_YOUR_BUDGET"

    }

    fun getCurrency(): Int {
        return sharedPref.getInt(KEY_CURRENCY, 0)
    }

    fun saveCurrency(id: Int) {
        sharedPref.edit().putInt(KEY_CURRENCY, id).apply()
    }

    fun getCheckCurrency(): Boolean {
        return sharedPref.getBoolean(KEY_CHECK_CURRENCY, false)
    }

    fun saveCheckCurrency(budget: Boolean) {
        sharedPref.edit().putBoolean(KEY_CHECK_CURRENCY, budget).apply()
    }

    fun getYourBudget(): Boolean {
        return sharedPref.getBoolean(KEY_YOUR_BUDGET, false)
    }

    fun saveYourBudget(budget: Boolean) {
        sharedPref.edit().putBoolean(KEY_YOUR_BUDGET, budget).apply()
    }
}

