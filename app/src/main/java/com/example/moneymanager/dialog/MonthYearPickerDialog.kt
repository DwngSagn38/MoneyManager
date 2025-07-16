package com.example.moneymanager.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseDialog
import com.example.moneymanager.databinding.DialogMonthYearPickerBinding
import com.example.moneymanager.widget.tap
import java.util.Calendar

class MonthYearPickerDialog(
    activity1: Activity,
    private val initialYear: Int,
    private val initialMonth: Int,
    private var sortByYear: Boolean,
    private val onDateSelected: (year: Int, month: Int, sortByYear : Boolean) -> Unit
) : BaseDialog<DialogMonthYearPickerBinding>(activity1, true) {

    private var selectedYear = initialYear
    private var selectedMonth = initialMonth

    private fun highlightSelectedMonth(grid: GridLayout, selectedIndex: Int) {
        for (i in 0 until grid.childCount) {
            val monthView = grid.getChildAt(i) as TextView
            monthView.setBackgroundResource(
                if (i == selectedIndex) R.drawable.bg_btn else android.R.color.transparent
            )
            monthView.setTextColor(
                if (i == selectedIndex)
                    activity.getColor(R.color.white)
                else
                    activity.getColor(R.color.color_C3C3C3)
            )
        }
    }


    override fun getContentView(): DialogMonthYearPickerBinding {
        return DialogMonthYearPickerBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        if (sortByYear){
            binding.rbYear.isChecked = true
            binding.tvThis.text = activity.getString(R.string.this_year)
            binding.gridMonths.visibility = View.GONE
        }else{
            binding.rbMonth.isChecked = true
            binding.tvThis.text = activity.getString(R.string.this_month)
            binding.gridMonths.visibility = View.VISIBLE
        }
        binding.tvYear.text = selectedYear.toString()
        highlightSelectedMonth(binding.gridMonths, selectedMonth)
    }

    override fun bindView() {
        binding.root.tap { dismiss() }
        binding.apply {

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnSave.setOnClickListener {
                onDateSelected(selectedYear, selectedMonth, sortByYear)
                dismiss()
            }

            btnPrevYear.setOnClickListener {
                selectedYear--
                tvYear.text = selectedYear.toString()
            }

            btnNextYear.setOnClickListener {
                selectedYear++
                tvYear.text = selectedYear.toString()
            }

            // Chọn tháng
            for (i in 0 until gridMonths.childCount) {
                val monthView = gridMonths.getChildAt(i) as TextView
                monthView.setOnClickListener {
                    selectedMonth = i
                    highlightSelectedMonth(gridMonths, i)
                }
            }

            tvThisMonth.setOnClickListener {
                selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
                selectedYear = Calendar.getInstance().get(Calendar.YEAR)
                tvYear.text = selectedYear.toString()
                highlightSelectedMonth(gridMonths, selectedMonth)
            }

            rdoGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rbYear -> {
                        tvThis.text = activity.getString(R.string.this_year)
                        gridMonths.visibility = View.GONE
                        sortByYear = true
                    }
                    R.id.rbMonth -> {
                        tvThis.text = activity.getString(R.string.this_month)
                        gridMonths.visibility = View.VISIBLE
                        sortByYear = false
                    }
                }
            }

        }

    }
}
