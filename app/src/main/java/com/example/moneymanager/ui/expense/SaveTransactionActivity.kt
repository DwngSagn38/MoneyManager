package com.example.moneymanager.ui.expense

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import com.example.moneymanager.R
import com.example.moneymanager.base.BaseActivity
import com.example.moneymanager.databinding.ActivitySaveTransactionBinding
import com.example.moneymanager.model.Category
import com.example.moneymanager.model.TransactionEntity
import com.example.moneymanager.viewmodel.SaveTransactionViewModel

class SaveTransactionActivity : BaseActivity<ActivitySaveTransactionBinding>() {
    private val viewModel: SaveTransactionViewModel by viewModels()

    override fun setViewBinding(): ActivitySaveTransactionBinding {
        return ActivitySaveTransactionBinding.inflate(layoutInflater)
    }

    override fun initView() {

        val category = intent.getSerializableExtra("CATEGORY_DATA") as? Category
        if (category != null) {
            binding.ivDisplay.setImageResource(category.imgResId)
            binding.tvName.text = category.name
        }

        val white = Color.parseColor("#FFFFFF")
        val gray = Color.parseColor("#8B8888")

        binding.hourPicker.minValue = 0
        binding.hourPicker.maxValue = 23
        binding.minutePicker.minValue = 0
        binding.minutePicker.maxValue = 59

        setupNumberPickerColors(binding.hourPicker, white, gray)
        setupNumberPickerColors(binding.minutePicker, white, gray)
        removeDivider(binding.hourPicker)
        removeDivider(binding.minutePicker)

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        binding.yearPicker.minValue = 1900
        binding.yearPicker.maxValue = 2100
        binding.yearPicker.value = currentYear

        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        binding.monthPicker.minValue = 0
        binding.monthPicker.maxValue = monthNames.size - 1
        binding.monthPicker.displayedValues = monthNames
        binding.monthPicker.value = 0

        updateDayPicker()
        setupNumberPickerColors(binding.dayPicker, white, gray)
        setupNumberPickerColors(binding.monthPicker, white, gray)
        setupNumberPickerColors(binding.yearPicker, white, gray)

        removeDivider(binding.dayPicker)
        removeDivider(binding.monthPicker)
        removeDivider(binding.yearPicker)

        binding.monthPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }
        binding.yearPicker.setOnValueChangedListener { _, _, _ -> updateDayPicker() }
    }

    private fun updateDayPicker() {
        val month = binding.monthPicker.value + 1
        val year = binding.yearPicker.value

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }

        val currentDay = binding.dayPicker.value
        binding.dayPicker.minValue = 1
        binding.dayPicker.maxValue = daysInMonth

        if (currentDay > daysInMonth) {
            binding.dayPicker.value = daysInMonth
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    private fun removeDivider(picker: NumberPicker) {
        try {
            val fields = NumberPicker::class.java.declaredFields
            for (field in fields) {
                if (field.name == "mSelectionDivider") {
                    field.isAccessible = true
                    field.set(picker, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun viewListener() {
        binding.tvAdd.setOnClickListener {
            val category = intent.getSerializableExtra("CATEGORY_DATA") as? Category
            if (category == null) {
                showToast(getString(R.string.error_missing_category))
                return@setOnClickListener
            }

            val note = binding.edtNote.text.toString().trim()
            val amount = binding.edtAmount.text.toString().trim()

            if (note.isEmpty()) {
                showToast(getString(R.string.error_empty_note))
                return@setOnClickListener
            }

            if (amount.isEmpty()) {
                showToast(getString(R.string.error_empty_amount))
                return@setOnClickListener
            }

            val time = String.format("%02d:%02d", binding.hourPicker.value, binding.minutePicker.value)
            val date = String.format(
                "%02d/%02d/%04d",
                binding.dayPicker.value,
                binding.monthPicker.value + 1,
                binding.yearPicker.value
            )

            val transaction = TransactionEntity(
                idCatagory = category.stt,
                img = category.imgResId,
                name = category.name,
                note = note,
                time = time,
                date = date,
                amount = amount.toFloat(),
                type = category.type
            )

            viewModel.saveTransaction(transaction)
            showToast(getString(R.string.success_transaction_saved))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun setupNumberPickerColors(picker: NumberPicker, centerColor: Int, outerColor: Int) {
        try {
            val typeface = ResourcesCompat.getFont(this, R.font.opensans_600)

            val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.isAccessible = true
            val selectorPaint = selectorWheelPaintField.get(picker) as Paint
            selectorPaint.color = outerColor
            selectorPaint.typeface = typeface

            picker.setFormatter { i -> String.format("%02d", i) }

            fun updateTextColor() {
                for (i in 0 until picker.childCount) {
                    val child = picker.getChildAt(i)
                    if (child is EditText) {
                        val currentText = child.text.toString()
                        val pickerValue = if (picker.displayedValues != null) {
                            picker.displayedValues[picker.value]
                        } else {
                            String.format("%02d", picker.value)
                        }
                        child.setTextColor(if (currentText == pickerValue) {
                            centerColor
                        } else {
                            outerColor
                        })

                        child.setTypeface(typeface)
                    }
                }
            }

            picker.setOnValueChangedListener { _, _, _ -> picker.post { updateTextColor() } }
            picker.setOnScrollListener { _, _ -> picker.post { updateTextColor() } }

            picker.post { updateTextColor() }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun dataObservable() {}
}
