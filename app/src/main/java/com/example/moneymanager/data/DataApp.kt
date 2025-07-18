package com.example.moneymanager.data

import com.example.moneymanager.R
import com.example.moneymanager.model.CurrencyModel

object DataApp {
    private var currency: CurrencyModel? = null

    fun setCurrency(currency: CurrencyModel) {
        this.currency = currency
    }
    fun getCurrency(): CurrencyModel {
        return currency ?: getListCurrency()[0]
    }
    fun getListCurrency() : List<CurrencyModel> {
        return listOf(
            CurrencyModel(0,R.drawable.fl_gbp_uk_pound,"GBP", " - UK Pound", "£"),
            CurrencyModel(1,R.drawable.fl_eur_euro,"EUR", " - Euro", "€"),
            CurrencyModel(2,R.drawable.fl_aud_australian_dollar,"AUD", " - Australian Dollar", "$"),
            CurrencyModel(3,R.drawable.fl_usd_us_dollar,"USD", " - US Dollar", "$"),
            CurrencyModel(4,R.drawable.fl_cny_chinese_yuan,"CNY", " - Chinese Yuan", "¥"),
            CurrencyModel(5,R.drawable.fl_inr_indian_rupee,"INR", " - Indian Rupee", "₹"),
            CurrencyModel(6,R.drawable.fl_vnd_vietnamese_dong,"VND", " - Vietnamese Dong", "đ"),
            CurrencyModel(7,R.drawable.fl_thb_thai_baht,"THB", " - Thai Baht", "B"),
            CurrencyModel(8,R.drawable.fl_idr_indonesia_rupiah,"IDR", " - Indonesian Rupiah", "Rp"),
        )
    }
}