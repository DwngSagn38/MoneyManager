package com.example.moneymanager.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.sin

class CircularWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var targetProgress: Float = 0f
    private var waveShift = 0f

    var progress: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 100f)
            invalidate()
        }

    var waveColor: Int = Color.parseColor("#848CFF") // Màu sóng
        set(value) {
            field = value
            wavePaint.color = value
            invalidate()
        }

    var backgroundCircleColor: Int = Color.parseColor("#3F3D56") // Màu nền
        set(value) {
            field = value
            backgroundPaint.color = value
            invalidate()
        }

    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = waveColor
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backgroundCircleColor
    }

    private val path = Path()
    private val waveHeight = 20f
    private val waveLength = 200f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = width.coerceAtMost(height) / 2f
        val centerX = width / 2f
        val centerY = height / 2f

        // Clip to circle
        canvas.save()
        canvas.clipPath(Path().apply {
            addCircle(centerX, centerY, radius, Path.Direction.CCW)
        })

        // Draw background
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // Calculate water level
        val waterLevel = height * (1 - progress / 100f)

        // Create wave path
        path.reset()
        path.moveTo(0f, waterLevel)
        var x = 0f
        while (x <= width + waveLength) {
            val y = (waveHeight * sin((2.0 * Math.PI * ((x + waveShift) / waveLength))).toFloat()) + waterLevel
            path.lineTo(x, y)
            x += 1f
        }

        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()

        // Draw wave
        canvas.drawPath(path, wavePaint)

        canvas.restore()
    }

    fun animateToProgress(value: Float) {
        targetProgress = value.coerceIn(0f, 100f)
        val animator = ValueAnimator.ofFloat(100f, targetProgress)
        animator.duration = 2000L
        animator.addUpdateListener {
            progress = it.animatedValue as Float
        }
        animator.start()
    }

    private var waveAnimator: ValueAnimator? = null

    fun startWaveAnimation() {
        waveAnimator?.cancel()
        waveAnimator = ValueAnimator.ofFloat(0f, waveLength)
        waveAnimator?.duration = 2000L
        waveAnimator?.repeatCount = ValueAnimator.INFINITE
        waveAnimator?.interpolator = LinearInterpolator()
        waveAnimator?.addUpdateListener {
            waveShift = it.animatedValue as Float
            invalidate()
        }
        waveAnimator?.start()
    }

    private val Float.dp get() = this * resources.displayMetrics.density

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator?.cancel()
    }
}

