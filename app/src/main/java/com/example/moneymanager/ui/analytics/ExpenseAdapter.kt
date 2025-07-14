package com.example.moneymanager.ui.analytics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.model.ExpenseCategory

class ExpenseAdapter(private val list: List<ExpenseCategory>) :
    RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    // Tính tổng chi tiêu để dùng cho progress
    private val totalAmount = list.sumOf { it.amount.toDouble() }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.imgIcon.setImageResource(item.iconRes)
        holder.tvName.text = item.name
        holder.tvAmount.text = "$${item.amount.toInt()}"

        // Tính phần trăm cho ProgressBar
        val percent = if (totalAmount > 0f) {
            ((item.amount / totalAmount) * 100).toInt()
        } else 0

        holder.progressBar.progress = percent
    }
}
