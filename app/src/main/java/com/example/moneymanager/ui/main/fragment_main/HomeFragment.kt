package com.example.moneymanager.ui.main.fragment_main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.example.moneymanager.dialog.MonthYearPickerDialog
import com.example.moneymanager.ui.main.adapter.HomePagerAdapter
import com.example.moneymanager.ui.main.fragment_main.fragment_home.ExpensesFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.IncomeFragment
import com.example.moneymanager.ui.main.fragment_main.fragment_home.LoansFragment
import com.example.moneymanager.utils.extensions.collectInLifecycle
import com.example.moneymanager.view.base.BaseFragment
import com.example.moneymanager.viewmodel.SaveTransactionViewModel
import java.util.Calendar
import com.example.moneymanager.viewmodel.SaveTransactionViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private var selectedMonth = 0
    private var selectedYear = 0
    private var sortByYear = false
    private lateinit var viewModel: SaveTransactionViewModel
    private var isVisible = false
    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        viewModel = ViewModelProvider(requireActivity()).get(SaveTransactionViewModel::class.java)
        val calendar = Calendar.getInstance()
        selectedMonth = calendar.get(Calendar.MONTH)
        selectedYear = calendar.get(Calendar.YEAR)
        viewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                highlightTab(position)
            }
        })
        highlightTab(0)

        loadAll()
    }

    override fun viewListener() {
        binding.tvExpenses.setOnClickListener {
            binding.viewPager.currentItem = 0
        }
        binding.tvIncome.setOnClickListener {
            binding.viewPager.currentItem = 1
        }
        binding.tvLoans.setOnClickListener {
            binding.viewPager.currentItem = 2
        }

        binding.tvCalendar.setOnClickListener {
            val dialog = MonthYearPickerDialog(
                requireActivity(),
                selectedYear,
                selectedMonth,
                sortByYear
            ) { year, month, sort ->
                selectedYear = year
                selectedMonth = month
                sortByYear = sort
                Log.d("MonthYear", "Selected: $month/$year sort by year: $sortByYear")
                viewModel.selectedDate.value = Triple(selectedMonth, selectedYear, sortByYear)
            }
            dialog.show()
        }

        binding.imgVisible.setOnClickListener {
            isVisible = !isVisible
            if (isVisible) {
                binding.imgVisible.setImageResource(R.drawable.ic_eye_invisible)
                binding.tvBalance.text = "$ %.2f".format(viewModel.totalBalance.value)
            } else {
                binding.tvBalance.text = getString(R.string.invisible_balance)
                binding.imgVisible.setImageResource(R.drawable.ic_eye_visible)
            }
        }

    }

    private fun highlightTab(index: Int) {
        val hovers = listOf(binding.ivExpenses, binding.ivIncome, binding.ivLoans)
        hovers.forEachIndexed { i, imageView ->
            if (i == index) imageView.setImageResource(R.drawable.hover)
            else imageView.setImageDrawable(null)
        }
    }

    override fun dataObservable() {
        viewModel.totalBalance.collectInLifecycle(viewLifecycleOwner) { balance ->
            if (isVisible) {
                binding.tvBalance.text = "$ %.2f".format(balance)
            }
        }
    }


    private fun loadAll(){
        viewModel.loadTransactionsByType("Income")
        viewModel.loadTransactionsByType("Expense")
        viewModel.loadTransactionsByType("Loans")
    }

    override fun onResume() {
        super.onResume()
        loadAll()
    }
}
