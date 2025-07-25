package com.example.moneymanager.ui.main.fragment_main

// HomeFragment.kt

import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.FragmentTransactionBinding
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import com.example.moneymanager.widget.gone
import com.example.moneymanager.widget.visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionFragment : BaseFragment<FragmentTransactionBinding>() {

    override fun setViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTransactionBinding {
        return FragmentTransactionBinding.inflate(layoutInflater)
    }

    private val calendar: Calendar = Calendar.getInstance()
    private var selectedView: TextView? = null
    private lateinit var viewModel: SaveTransactionViewModel
    private var currentMonth = 0
    private var currentYear = 0
    private var currentDay = 0

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity()).get(SaveTransactionViewModel::class.java)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        currentMonth = calendar.get(Calendar.MONTH)
        currentYear = calendar.get(Calendar.YEAR)

        viewModel.filterTransactionsByDaily(currentDay,currentMonth,currentYear)
        setDataTotal()
        binding.calendarGrid.post {
            setupCalendar()
        }

    }


    override fun viewListener() {
        binding.btnPrevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            currentMonth = calendar.get(Calendar.MONTH)
            currentYear = calendar.get(Calendar.YEAR)
            setupCalendar()
        }

        binding.btnNextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            currentMonth = calendar.get(Calendar.MONTH)
            currentYear = calendar.get(Calendar.YEAR)
            setupCalendar()
        }
    }


    private fun setupCalendar() {
        val today = Calendar.getInstance()

        binding.tvMonth.text = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        binding.tvYear.text = currentYear.toString()

        // 1. Đặt ngày đầu tiên của tháng
        val firstDayCalendar = calendar.clone() as Calendar
        firstDayCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = firstDayCalendar.get(Calendar.DAY_OF_WEEK)
        val totalDaysInMonth = firstDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - Calendar.MONDAY

        binding.calendarGrid.removeAllViews()

        // 2. Tính ngày cuối của tháng trước
        val prevMonth = firstDayCalendar.clone() as Calendar
        prevMonth.add(Calendar.MONTH, -1)
        val totalDaysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        // 3. Hiển thị ngày cuối của tháng trước (nếu có)
        for (i in offset downTo 1) {
            val day = totalDaysInPrevMonth - i + 1
            setDayOfMonth(day, isToday = false, isFromOtherMonth = true)
        }

        // 4. Hiển thị các ngày trong tháng hiện tại
        for (day in 1..totalDaysInMonth) {
            val isToday = (today.get(Calendar.YEAR) == currentYear &&
                    today.get(Calendar.MONTH) == currentMonth &&
                    today.get(Calendar.DAY_OF_MONTH) == day)
            setDayOfMonth(day, isToday, isFromOtherMonth = false)
        }

        // 5. Tính số ô còn thiếu để đủ 6 hàng x 7 cột = 42 ô
        val totalCells = offset + totalDaysInMonth
        val nextDays = 42 - totalCells
        for (day in 1..nextDays) {
            setDayOfMonth(day, isToday = false, isFromOtherMonth = true)
        }
    }



    private fun setDayOfMonth(dayNumber: Int, isToday: Boolean, isFromOtherMonth: Boolean) {
        val context = requireContext()

        val dayView = TextView(context).apply {
            text = dayNumber.toString()
            gravity = Gravity.CENTER
            setPadding(8, 8, 8, 8)

            val params = GridLayout.LayoutParams().apply {
                width = binding.calendarGrid.width / 7
                height = binding.calendarGrid.height / 6
            }
            layoutParams = params

            // Màu chữ cho ngày thuộc/không thuộc tháng hiện tại
            setTextColor(
                if (isFromOtherMonth) Color.parseColor("#66FFFFFF") // màu xám nhạt
                else Color.WHITE
            )

            // Nếu là hôm nay => đánh dấu selected mặc định
            if (isToday && !isFromOtherMonth) {
                background = context.getDrawable(R.drawable.bg_btn)
                selectedView = this
            }

            // Chỉ chọn được ngày thuộc tháng hiện tại
            if (!isFromOtherMonth) {
                setOnClickListener {
                    selectedView?.background = null
                    background = context.getDrawable(R.drawable.bg_btn)
                    selectedView = this
                    currentDay = dayNumber
                    viewModel.filterTransactionsByDaily(dayNumber, currentMonth, currentYear)
                    setDataTotal()
                }
            }
        }

        binding.calendarGrid.addView(dayView)
    }

    private fun setDataTotal(){
        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            Log.d("TransactionFragment", "Filtered transactions: $transactions")
            val income = transactions.filter { it.type == "Income" }.sumOf { it.amount.toDouble() }
            val expense = transactions.filter { it.type == "Expense" }.sumOf { it.amount.toDouble() }
            if (transactions.isEmpty() || income == 0.0 && expense == 0.0) {
                binding.llTotal.gone()
                binding.clEmpty.visible()
            }else{
                binding.llTotal.visible()
                binding.clEmpty.gone()
                val balance = income - expense
                binding.apply {
                    tvTotalIncome.text = formatCurrency(income, DataApp.getCurrency().country)
                    tvTotalExpanse.text = formatCurrency(expense, DataApp.getCurrency().country)
                    tvTotalBalance.text = formatCurrency(balance, DataApp.getCurrency().country)
                }
                if (balance > 0){
                    binding.tvTotalBalance.setTextColor(Color.parseColor("#00FF6F"))
                }else{
                    binding.tvTotalBalance.setTextColor(Color.parseColor("#FF0000"))
                }
            }

        }
    }


    override fun dataObservable() {}

    override fun onResume() {
        super.onResume()
        viewModel.filterTransactionsByDaily(currentDay,currentMonth,currentYear)
    }
}

