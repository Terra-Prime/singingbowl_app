package com.singingbowl.app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val waveformPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }
    
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = Color.parseColor("#2A2A2A")
        isAntiAlias = true
    }
    
    private val path = Path()
    private var waveformData: FloatArray? = null
    private var waveformColor = Color.parseColor("#00FF88")
    
    // Channel colors
    private val channelColors = arrayOf(
        Color.parseColor("#FF6B6B"),  // Channel 1 - Red
        Color.parseColor("#4ECDC4"),  // Channel 2 - Cyan
        Color.parseColor("#45B7D1"),  // Channel 3 - Blue
        Color.parseColor("#96CEB4"),  // Channel 4 - Green
        Color.parseColor("#FFEAA7")   // BGM - Yellow
    )
    
    fun setWaveformData(data: FloatArray?) {
        waveformData = data
        invalidate()
    }
    
    fun setChannelColor(channelIndex: Int) {
        waveformColor = if (channelIndex in channelColors.indices) {
            channelColors[channelIndex]
        } else {
            Color.parseColor("#00FF88")
        }
        waveformPaint.color = waveformColor
        invalidate()
    }
    
    fun setCustomColor(color: Int) {
        waveformColor = color
        waveformPaint.color = waveformColor
        invalidate()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val w = width.toFloat()
        val h = height.toFloat()
        val centerY = h / 2
        
        // Draw grid
        canvas.drawLine(0f, centerY, w, centerY, gridPaint)
        canvas.drawLine(0f, h * 0.25f, w, h * 0.25f, gridPaint)
        canvas.drawLine(0f, h * 0.75f, w, h * 0.75f, gridPaint)
        
        // Draw waveform
        waveformData?.let { data ->
            if (data.isEmpty()) return@let
            
            path.reset()
            val step = data.size.toFloat() / w
            var x = 0f
            
            path.moveTo(0f, centerY - data[0] * centerY * 0.8f)
            
            var i = 0f
            while (i < data.size) {
                val index = i.toInt().coerceIn(0, data.lastIndex)
                val y = centerY - data[index] * centerY * 0.8f
                path.lineTo(x, y)
                x += 1f
                i += step
            }
            
            waveformPaint.color = waveformColor
            canvas.drawPath(path, waveformPaint)
        }
    }
}
