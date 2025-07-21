package com.example.moneymanager.ui.analytics.adapter

import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.model.TransactionEntity

class Expense1Adapter(
    private var list: List<TransactionEntity>,
    private var totalAmount: Float
) :
    RecyclerView.Adapter<Expense1Adapter.ViewHolder>() {

    // Tính tổng chi tiêu để dùng cho progress
    fun updateData(newList: List<TransactionEntity>) {
        list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewColor: View = itemView.findViewById(R.id.viewColor)
        val tvLegendName: TextView = itemView.findViewById(R.id.tvLegendName)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val tvLegendPercent: TextView = itemView.findViewById(R.id.tvLegendPercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense1, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvLegendName.text = item.name
        val circleDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(item.color)
        }
        holder.viewColor.background = circleDrawable

        holder.progressBar.progress = ((item.amount.toFloat() / totalAmount) * 100).toInt()
        holder.tvLegendPercent.text = "${"%.0f".format((item.amount.toFloat() / totalAmount) * 100)}%"
        // Tạo progressDrawable động
        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8f
            setColor(Color.parseColor("#565491")) // màu nền
        }

        val progressDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8f
            setColor(item.color) // màu theo category
        }

        val clipDrawable = ClipDrawable(progressDrawable, Gravity.START, ClipDrawable.HORIZONTAL)
        val layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, clipDrawable)).apply {
            setId(0, android.R.id.background)
            setId(1, android.R.id.progress)
        }

        holder.progressBar.progressDrawable = layerDrawable
    }

}
