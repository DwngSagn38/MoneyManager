package com.example.moneymanager.ui.annual_category_report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ItemMonthAmountBinding
import com.example.moneymanager.utils.extensions.formatCurrency

data class MonthAmount(val month: Int, val amount: Double)

class MonthlyAmountAdapter(private val data: List<MonthAmount>) :
    RecyclerView.Adapter<MonthlyAmountAdapter.MonthViewHolder>() {

    class MonthViewHolder(val binding: ItemMonthAmountBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMonthAmountBinding.inflate(inflater, parent, false)
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val item = data[position]
        val context = holder.itemView.context
        val monthName = getMonthName(context, item.month)

        holder.binding.tvMonth.text = monthName
        holder.binding.tvAmount.text = formatCurrency(item.amount, DataApp.getCurrency().country)
    }

    override fun getItemCount() = data.size

    private fun getMonthName(context: Context, month: Int): String {
        return when (month) {
            1 -> context.getString(R.string.january)
            2 -> context.getString(R.string.february)
            3 -> context.getString(R.string.march)
            4 -> context.getString(R.string.april)
            5 -> context.getString(R.string.may)
            6 -> context.getString(R.string.june)
            7 -> context.getString(R.string.july)
            8 -> context.getString(R.string.august)
            9 -> context.getString(R.string.september)
            10 -> context.getString(R.string.october)
            11 -> context.getString(R.string.november)
            12 -> context.getString(R.string.december)
            else -> "Unknown"
        }
    }
}
