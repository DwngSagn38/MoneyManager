package com.example.moneymanager.ui.analytics.adapter

import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.model.TransactionEntity

class ExpenseAdapter(
    private var list: List<TransactionEntity>,
    private val onItemClicked: (TransactionEntity) -> Unit
    ) :
    RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    private var totalAmount = list.sumOf { it.amount.toDouble() }

    fun updateData(newList: List<TransactionEntity>) {
        list = newList
        notifyDataSetChanged()
    }

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
        holder.itemView.setOnClickListener { onItemClicked(item) }
        holder.tvName.text = item.name
        holder.tvAmount.text = "$${item.amount.toFloat()}"
        holder.imgIcon.setImageResource(item.img)

        holder.progressBar.progress = ((item.amount.toFloat() / totalAmount) * 100).toInt()

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
