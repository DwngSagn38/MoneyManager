package com.example.moneymanager.ui.main.fragment_main.fragment_home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.databinding.ItemDateHeaderBinding
import com.example.moneymanager.databinding.ItemTransitionMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ExpensesAdapter(
    private var items: List<ExpenseListItem>,
    private var check: Boolean?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    inner class HeaderViewHolder(val binding: ItemDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(header: ExpenseListItem.DateHeader, isFirst: Boolean) {
            // Format ngày
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            val date = inputFormat.parse(header.date)
            binding.tvDate.text = outputFormat.format(date!!)

            // Format số tiền
            val isNegative = header.totalAmount < 0
            val amount = abs(header.totalAmount)
            val amountFormatted = if (amount % 1.0 == 0.0) {
                amount.toInt().toString()
            } else {
                String.format("%.2f", amount)
            }

            val displayAmount = if (isNegative) "-$$amountFormatted" else "$$amountFormatted"
            binding.tvTotalAmount.text = displayAmount

            if (check == true) {
                val color = if (isNegative) "#FF2E2E" else "#00FF6F"
                binding.tvTotalAmount.setTextColor(Color.parseColor(color))
            }

            // Divider
            binding.viewDivider.visibility = if (isFirst) View.GONE else View.VISIBLE
        }
    }


    inner class ItemViewHolder(val binding: ItemTransitionMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExpenseListItem.ExpenseItem) {
            val transaction = item.transaction
            binding.ivDisplay.setImageResource(transaction.img)
            binding.tvName.text = transaction.name
            val amount = transaction.amount.toDouble()
            val formatted = if (amount % 1.0 == 0.0) {
                "${amount.toInt()} $"
            } else {
                "$${String.format("%.2f", amount)}"
            }
            binding.tvAmount.text = formatted
            binding.tvTime.text = transaction.time
            if (check == true) {
                if (transaction.check) {
                    binding.tvAmount.setTextColor(Color.parseColor("#02BB53"))
                } else {
                    binding.tvAmount.setTextColor(Color.parseColor("#FF2E2E"))
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ExpenseListItem.DateHeader -> TYPE_HEADER
            is ExpenseListItem.ExpenseItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }

            else -> {
                val binding = ItemTransitionMainBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ItemViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ExpenseListItem.DateHeader -> {
                val isFirstHeader =
                    position == items.indexOfFirst { it is ExpenseListItem.DateHeader }
                (holder as HeaderViewHolder).bind(item, isFirstHeader)
            }

            is ExpenseListItem.ExpenseItem -> (holder as ItemViewHolder).bind(item)
        }
    }


    fun submitList(newList: List<ExpenseListItem>) {
        items = newList
        notifyDataSetChanged()
    }
}
