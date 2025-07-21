package com.example.moneymanager.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CircularProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var progress: Float = 0f // value từ 0f đến 100f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    private val strokeWidth = 16f.dp
    private val rectF = RectF()

    private val paintBackground = Paint().apply {
        color = Color.parseColor("#33FFFFFF") // Vòng tròn nền xám nhạt
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        isAntiAlias = true
    }

    private val paintProgress = Paint().apply {
        color = Color.parseColor("#848CFF") // Màu vòng tròn chính
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = strokeWidth / 2
        rectF.set(padding, padding, width - padding, height - padding)

        // Vẽ vòng tròn nền
        canvas.drawArc(rectF, 0f, 360f, false, paintBackground)

        // Vẽ vòng tròn tiến độ
        val sweepAngle = 360f * (progress / 100f)
        canvas.drawArc(rectF, -90f, sweepAngle, false, paintProgress)

    }

    private val Float.dp get() = this * resources.displayMetrics.density
}
