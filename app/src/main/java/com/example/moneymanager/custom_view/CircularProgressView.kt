package com.example.moneymanager.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var progress: Int = 0 // current progress 0-100
        set(value) {
            field = value
            invalidate()
        }

    private val strokeWidth = 12f.dp
    private val rectF = RectF()
    private val paintBg = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        isAntiAlias = true
    }

    private val paintProgress = Paint().apply {
        color = Color.parseColor("#14A1FB")
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = this@CircularProgressView.strokeWidth
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val padding = strokeWidth / 2
        rectF.set(padding, padding, width - padding, height - padding)
        canvas.drawArc(rectF, 0f, 360f, false, paintBg)
        val sweepAngle = 360f * (progress / 100f)
        canvas.drawArc(rectF, -90f, sweepAngle, false, paintProgress)
    }

    private val Float.dp get() = this * resources.displayMetrics.density
}
