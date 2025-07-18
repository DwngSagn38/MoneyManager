package com.example.moneymanager.custom_view

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val radius: Float = 30f // bo góc bao nhiêu tuỳ chỉnh
) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun initBuffers() {
        val barData = mChart.barData
        if (barData != null) {
            mBarBuffers = Array(barData.dataSetCount) { i ->
                BarBuffer(
                    barData.getDataSetByIndex(i).entryCount * 4,
                    barData.dataSetCount,
                    barData.getDataSetByIndex(i).isStacked
                )
            }
        }
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        if (index >= mBarBuffers.size) return
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f

        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val barData = mChart.barData

        mBarBuffers[index].apply {
            setPhases(phaseX, phaseY)
            setDataSet(index)
            setInverted(mChart.isInverted(dataSet.axisDependency))
            setBarWidth(barData.barWidth)
            feed(dataSet)
        }

        val buffer = mBarBuffers[index]

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.colors.size == 1

        for (j in buffer.buffer.indices step 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break

            val top = buffer.buffer[j + 1]
            val bottom = buffer.buffer[j + 3]
            val left = buffer.buffer[j]
            val right = buffer.buffer[j + 2]

            mRenderPaint.color = if (isSingleColor) dataSet.color else dataSet.getColor(j / 4)

            val rectF = RectF(left, top, right, bottom)

            // Vẽ cột có bo góc trên
            val path = android.graphics.Path().apply {
                reset()
                moveTo(rectF.left, rectF.bottom)
                lineTo(rectF.left, rectF.top + radius)
                quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top)
                lineTo(rectF.right - radius, rectF.top)
                quadTo(rectF.right, rectF.top, rectF.right, rectF.top + radius)
                lineTo(rectF.right, rectF.bottom)
                close()
            }
            c.drawPath(path, mRenderPaint)


            if (drawBorder) {
                c.drawPath(path, mBarBorderPaint)
            }
        }
    }
}
