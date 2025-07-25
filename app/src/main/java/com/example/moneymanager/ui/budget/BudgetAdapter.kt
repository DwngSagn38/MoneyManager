package com.example.moneymanager.ui.budget

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseAdapter
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.ItemBudgetBinding
import com.example.moneymanager.model.BudgetModel
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.utils.extensions.formatCurrency

class BudgetAdapter(
    val listExpense : List<TransactionEntity>,
    val onClick: (budget: BudgetModel, spent : Float, color : Int) -> Unit,
    val onEdit: (budget: BudgetModel) -> Unit
) : BaseAdapter<ItemBudgetBinding, BudgetModel>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemBudgetBinding {
        return ItemBudgetBinding.inflate(inflater, parent, false)
    }

    private var transactionEntity : TransactionEntity? = null
    private var remainingPercent = 100f

    override fun creatVH(binding: ItemBudgetBinding): RecyclerView.ViewHolder = CurrencyVH(binding)

    inner class CurrencyVH(binding: ItemBudgetBinding) : BaseVH<BudgetModel>(binding){
        override fun onItemClickListener(data: BudgetModel) {
            super.onItemClickListener(data)
        }


        override fun bind(data: BudgetModel) {
            super.bind(data)
            var color = 0
            var spent = 0f
            val listExpenseByName = listExpense.filter { it.name == data.name }
            if (listExpenseByName.isEmpty()) spent = 0f
            else {
                spent = listExpenseByName.sumOf { it.amount.toDouble() }.toFloat()
                color = listExpenseByName[0].color
                transactionEntity = listExpenseByName[0]
            }

            remainingPercent = if (data.budget > 0f) {
                ((data.budget - spent) / data.budget * 100f).coerceIn(0f, 100f)
            } else 0f

            binding.circleProgress.apply {
                progress = remainingPercent
                waveColor = if (remainingPercent == 100f) Color.parseColor("#848CFF") else color
                animateToProgress(remainingPercent)
                startWaveAnimation()
            }

            binding.apply {
                tvBudget.text = formatCurrency(data.budget.toDouble(), DataApp.getCurrency().country)
                tvBudgetName.text = data.name
                tvPercent.text = "${String.format("%.2f", remainingPercent)}%"
            }

            binding.root.setOnClickListener {
                onClick(data, spent, color)
            }

            binding.imgEditBudget.setOnClickListener {
                onEdit(data)
            }
        }

    }

}