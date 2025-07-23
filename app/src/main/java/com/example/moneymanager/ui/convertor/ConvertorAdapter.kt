package com.example.moneymanager.ui.convertor


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.databinding.ItemConventorBinding
import com.example.moneymanager.model.ConventorModel

import com.example.moneymanager.widget.tap

class ConvertorAdapter(
    private var currencyList: MutableList<ConventorModel>,
    private val listener: OnConvertorClickListener
): RecyclerView.Adapter<ConvertorAdapter.CurrencyViewHoder>() {

    private var filteredList: MutableList<ConventorModel> = currencyList.toMutableList()

    inner class CurrencyViewHoder(private val binding: ItemConventorBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: ConventorModel) {
            binding.root.tap {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && position >= 0) {
                    listener.onItemClick(currency)
                    updateSelection(position)
                } else {
                    Log.e("CurrencyAdapter", "Vị trí không hợp lệ: $position")
                }
            }

            binding.tvOption.text = currency.content
            if (currency.isSelected) {
                binding.checkboxSelected.visibility = View.VISIBLE
                binding.checkboxNoSelected.visibility = View.GONE
            } else {
                binding.checkboxSelected.visibility = View.GONE
                binding.checkboxNoSelected.visibility = View.VISIBLE
            }
        }
    }
    fun updateList(newList: MutableList<ConventorModel>) {
        currencyList = newList
        notifyDataSetChanged()
    }
    fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            currencyList.toMutableList()
        } else {
            currencyList.filter {
                it.content.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun updateSelection(selectedPosition: Int) {
        if (selectedPosition >= 0 && selectedPosition < filteredList.size) {
            val selectedCurrency = filteredList[selectedPosition]
            val previousSelectedPosition = currencyList.indexOfFirst { it.isSelected }
            currencyList = currencyList.map {
                it.copy(isSelected = it == selectedCurrency)
            }.toMutableList()
            filterList("")
            if (previousSelectedPosition >= 0) {
                val previousFilteredPosition = filteredList.indexOfFirst {
                    it == currencyList[previousSelectedPosition]
                }
                if (previousFilteredPosition >= 0) notifyItemChanged(previousFilteredPosition)
            }
            val newFilteredPosition = filteredList.indexOfFirst {
                it == selectedCurrency
            }
            if (newFilteredPosition >= 0) notifyItemChanged(newFilteredPosition)
        } else {
            Log.e("CurrencyAdapter", "Vị trí không hợp lệ: $selectedPosition")
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHoder {
        val binding = ItemConventorBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CurrencyViewHoder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: CurrencyViewHoder, position: Int) {
        holder.bind(filteredList[position])
    }
}