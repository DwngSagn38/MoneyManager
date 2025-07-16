package com.example.moneymanager.ui.analytics

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymanager.R
import com.example.moneymanager.model.ExpenseCategory

class SharedAnalyticsViewModel : ViewModel() {

    val selectedDate = MutableLiveData<Triple<Int, Int, Boolean>>() // month, year, sortByYear

    // Giả lập dữ liệu gốc (có ngày/tháng/năm thực tế)
    private var categories = listOf(
        ExpenseCategory("Transportation", 900f, Color.parseColor("#627BFF"), R.drawable.ic_expenses, getRandomDate()),
        ExpenseCategory("Pets", 200f, Color.parseColor("#FF6F91"), R.drawable.ic_loans, getRandomDate()),
        ExpenseCategory("Education", 160f, Color.parseColor("#46D9F3"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Housing", 300f, Color.parseColor("#CB41C8"), R.drawable.ic_expenses, getRandomDate()),
        ExpenseCategory("Beauty", 1200f, Color.parseColor("#EF5267"), R.drawable.ic_loans, getRandomDate()),
        ExpenseCategory("Travel", 460f, Color.parseColor("#00BB80"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Books", 90f, Color.parseColor("#EF5252"), R.drawable.ic_expenses, getRandomDate()),
        ExpenseCategory("Lottery", 500f, Color.parseColor("#FF6F91"), R.drawable.ic_loans, getRandomDate()),
        ExpenseCategory("Other", 660f, Color.parseColor("#BB52EF"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Books", 90f, Color.parseColor("#EF5252"), R.drawable.ic_expenses, getRandomDate()),
        ExpenseCategory("Lottery", 500f, Color.parseColor("#FF6F91"), R.drawable.ic_loans, getRandomDate()),
        ExpenseCategory("Other", 660f, Color.parseColor("#BB52EF"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Travel", 460f, Color.parseColor("#00BB80"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Books", 90f, Color.parseColor("#EF5252"), R.drawable.ic_expenses, getRandomDate()),
        ExpenseCategory("Lottery", 500f, Color.parseColor("#FF6F91"), R.drawable.ic_loans, getRandomDate()),
        ExpenseCategory("Other", 660f, Color.parseColor("#BB52EF"), R.drawable.ic_income, getRandomDate()),
        ExpenseCategory("Books", 90f, Color.parseColor("#EF5252"), R.drawable.ic_expenses, getRandomDate()),
    )


    // LiveData chứa danh sách sau khi lọc
    val filteredCategories = MutableLiveData<List<ExpenseCategory>>()

    fun filterCategories(month: Int, year: Int, sortByYear: Boolean) {
        val filtered = categories.filter {
            val cal = java.util.Calendar.getInstance().apply {
                timeInMillis = it.createdAt
            }
            val itemMonth = cal.get(java.util.Calendar.MONTH) // 0-based (0 = Jan)
            val itemYear = cal.get(java.util.Calendar.YEAR)

            if (sortByYear) {
                itemYear == year
            } else {
                itemYear == year && itemMonth == month
            }
        }

        filteredCategories.value = filtered
    }

    private fun getRandomDate(): Long {
        val calendar = java.util.Calendar.getInstance()
        val startYear = 2024
        val endYear = 2025

        val year = (startYear..endYear).random()
        val month = (0..11).random() // 0 = Jan, 11 = Dec
        val day = (1..28).random() // Để tránh ngày không hợp lệ
        val hour = (0..23).random()
        val minute = (0..59).random()
        val second = (0..59).random()

        calendar.set(year, month, day, hour, minute, second)
        return calendar.timeInMillis
    }

}

