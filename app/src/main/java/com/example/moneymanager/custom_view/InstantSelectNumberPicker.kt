package com.example.moneymanager.custom_view
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker

class InstantSelectNumberPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : NumberPicker(context, attrs) {

    private var centerColor: Int = Color.WHITE
    private var outerColor: Int = Color.parseColor("#8B8888")

    private var previousValue: Int = value

    init {
        setWillNotDraw(false)
        setFormatter { i -> String.format("%02d", i) }
    }

    fun setColors(center: Int, outer: Int) {
        centerColor = center
        outerColor = outer
        updateChildColors()
    }

    private fun updateChildColors() {
        try {
//            val selectorWheelPaintField =
//                NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
//            selectorWheelPaintField.isAccessible = true
//            val selectorPaint = selectorWheelPaintField.get(this) as Paint
//            selectorPaint.color = outerColor

            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is EditText) {
                    child.setTextColor(centerColor)
                    child.textSize = 22f
                    child.setTypeface(null, android.graphics.Typeface.BOLD)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        super.scrollBy(x, y)
        updateIfValueChanged()
    }

    override fun computeScroll() {
        super.computeScroll()
        updateIfValueChanged()
    }

    private fun updateIfValueChanged() {
        if (previousValue != value) {
            previousValue = value
            updateChildColors() // Apply center color instantly
        }
    }
}
