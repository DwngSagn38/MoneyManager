package com.example.moneymanager.ui.currency

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseAdapter
import com.example.moneymanager.databinding.ItemCurrencyBinding
import com.example.moneymanager.model.CurrencyModel

class CurrencyAdapter (
    val onClick: (currency: CurrencyModel) -> Unit
) : BaseAdapter<ItemCurrencyBinding, CurrencyModel>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int,
    ): ItemCurrencyBinding {
        return ItemCurrencyBinding.inflate(inflater, parent, false)
    }

    var selectedPosition = -1

    override fun creatVH(binding: ItemCurrencyBinding): RecyclerView.ViewHolder = CurrencyVH(binding)

    inner class CurrencyVH(binding: ItemCurrencyBinding) : BaseVH<CurrencyModel>(binding){
        override fun onItemClickListener(data: CurrencyModel) {
            super.onItemClickListener(data)

            val previousPosition = selectedPosition
            selectedPosition = adapterPosition
            notifyItemChanged(previousPosition)  // cập nhật item cũ
            notifyItemChanged(selectedPosition) // cập nhật item mới

            onClick.invoke(data)
        }


        override fun bind(data: CurrencyModel) {
            super.bind(data)
            binding.apply {
                icLang.setImageResource(data.img)
                txtCountry.text = data.country
                txtName.text = data.name
                txtCurrency.text = data.currency

                if (selectedPosition == adapterPosition) {
                    layoutItem.setBackgroundResource(R.drawable.bg_item_currency_selected)
                } else {
                    layoutItem.setBackgroundResource(R.drawable.bg_item_currency)
                }
            }
        }

    }
}