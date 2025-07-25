package com.example.moneymanager.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.moneymanager.data.DataApp
import java.text.NumberFormat
import java.util.*

fun formatCurrency(amount: Double, currencyCode: String): String {
    return try {
        val currency = Currency.getInstance(currencyCode)
        val symbol = currency.symbol
        val format = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 0
        }
        "$symbol ${format.format(amount)}"
    } catch (e: Exception) {
        "$currencyCode ${String.format("%.2f", amount)}"
    }
}

fun formatCurrencyNotSymbol(amount: Double, currencyCode: String): String {
    return try {
        val currency = Currency.getInstance(currencyCode)
        val format = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 0
        }
        "${format.format(amount)}"
    } catch (e: Exception) {
        "$currencyCode ${String.format("%.2f", amount)}"
    }
}

fun formatDate(month : Int, year : Int) : String {
    return String.format("%02d/%d", month + 1, year)
}


fun formatEditText(view : EditText){
    view.addTextChangedListener(object : TextWatcher {
        private var current = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                view.removeTextChangedListener(this)

                val cleanString = s.toString().replace("[^\\d]".toRegex(), "")

                if (cleanString.isNotEmpty()) {
                    try {
                        val parsed = cleanString.toDouble()
                        val formatted = formatCurrencyNotSymbol(parsed, DataApp.getCurrency().country)
                        current = formatted
                        view.setText(formatted)
                        view.setSelection(formatted.length)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    current = ""
                    view.setText("")
                }

                view.addTextChangedListener(this)
            }
        }
    })
}

