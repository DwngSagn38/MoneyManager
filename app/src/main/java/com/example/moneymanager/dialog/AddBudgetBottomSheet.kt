package com.example.moneymanager.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseBottomSheetDialog
import com.example.moneymanager.data.DataApp
import com.example.moneymanager.databinding.DialogAddBudgetBinding
import com.example.moneymanager.databinding.DialogEditBudgetBinding
import com.example.moneymanager.model.BudgetModel
import com.example.moneymanager.model.Category
import com.example.moneymanager.ui.annual_category_report.AnnualCategoryReportActivity
import com.example.moneymanager.ui.expense.CategoryAdapter
import com.example.moneymanager.utils.extensions.formatCurrency
import com.example.moneymanager.utils.extensions.formatCurrencyNotSymbol
import com.example.moneymanager.utils.extensions.formatEditText
import com.example.moneymanager.widget.getCleanFloat
import com.google.android.material.bottomsheet.BottomSheetDialog

class AddBudgetBottomSheet(
    private val context: Context,
    private val fullList: List<Category>,
    private val remaining : Float,
    private val onSave: (Float, String) -> Unit
) : BaseBottomSheetDialog<DialogAddBudgetBinding>() {

    private var budgetName = ""

    override fun makeBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogAddBudgetBinding {
        return DialogAddBudgetBinding.inflate(inflater, container, false)
    }

    override fun setupView() {
        binding.apply {

            clBudgetName.setOnClickListener {
                showAddTransactionBottomSheet()
            }

            tvBudgetName.setOnClickListener {
                showAddTransactionBottomSheet()
            }

            img1.setOnClickListener {
                showAddTransactionBottomSheet()
            }

            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener {
                if (tvBudgetName.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(), getString(R.string.please_enter_the_budget_name), Toast.LENGTH_SHORT).show()
                }else if (edtBudget.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(), getString(R.string.please_enter_a_valid_number), Toast.LENGTH_SHORT).show()
                }else{
                    val newBudget = binding.edtBudget.getCleanFloat()
                    Log.d("AddBudgetBottomSheet", "Remaining: $remaining, Entered Budget: ${newBudget}")
                    if (newBudget > remaining){
                        Toast.makeText(requireContext(), getString(R.string.budget_cannot_exceed_remaining), Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("AddBudgetBottomSheet", "Saving budget: ${newBudget}")
                        onSave(newBudget, tvBudgetName.text.toString())
                        dismiss()
                    }
                }
            }
        }

        formatEditText(binding.edtBudget)

    }

    private fun showAddTransactionBottomSheet() {
        val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_choose_a_category, null)
        dialog.setContentView(view)

        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        val ivClose = view.findViewById<ImageView>(R.id.ic_close)
        val recyclerViewCategory = view.findViewById<RecyclerView>(R.id.recyclerViewCategory)

        val adapter = CategoryAdapter(fullList) { category ->
            budgetName = category.name
            binding.tvBudgetName.text = category.name
            dialog.dismiss()
        }
        recyclerViewCategory.layoutManager = GridLayoutManager(context, 4)
        recyclerViewCategory.adapter = adapter
        dialog.show()
    }
}
