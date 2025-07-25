package com.example.moneymanager.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseBottomSheetDialog
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.DialogEditBudgetBinding
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.extensions.formatCurrencyNotSymbol
import com.example.moneymanager.utils.extensions.formatEditText
import com.example.moneymanager.widget.getCleanFloat

class EditBudgetBottomSheet(
    private val currentBudget: Float,
    private val maxBudget: Float? = null,
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
            val newBudget = binding.edtBudget.getCleanFloat()

            if (newBudget != null) {
                if (maxBudget != null && newBudget.toString().toFloat() > maxBudget && newBudget.toString().toFloat() != 0f){
                    Toast.makeText(requireContext(), getString(R.string.budget_cannot_exceed_remaining), Toast.LENGTH_SHORT).show()
                }else{
                    onBudgetChanged(newBudget)
                    dismiss()
                }
            }else{
                onBudgetChanged(currentBudget)
                dismiss()
            }
        }

        formatEditText(binding.edtBudget)



    }
}
