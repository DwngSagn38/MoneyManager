package com.example.moneymanager.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.moneymanager.base.BaseBottomSheetDialog
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.DialogEditBudgetBinding
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.extensions.formatCurrencyNotSymbol

class EditBudgetBottomSheet(
    private val currentBudget: Float,
    private val onBudgetChanged: (Float) -> Unit
) : BaseBottomSheetDialog<DialogEditBudgetBinding>() {

    override fun makeBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogEditBudgetBinding {
        return DialogEditBudgetBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        // Hiển thị giá trị hiện tại
        binding.edtBudget.setHint(formatCurrencyNotSymbol(currentBudget.toDouble(), DataApp.getCurrency().country))
        binding.tvCurrency.text = DataApp.getCurrency().currency
        binding.btnSave.setOnClickListener {
            val input = binding.edtBudget.text.toString()
            val newBudget = input.toFloatOrNull()
            if (newBudget != null) {
                onBudgetChanged(newBudget)
            }else{
                onBudgetChanged(currentBudget)
            }
            dismiss()
        }


    }
}
