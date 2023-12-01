package com.example.hive.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.hive.R


class BeeView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var beeX: Float = 0f
    private var beeY: Float = 0f
    private var beeBitmap: Bitmap? = null

    init {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.bee_icon)
        beeBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, false)
    }

    fun setBeePosition(x: Float, y: Float) {
        beeX = x
        beeY = y
        invalidate() // Redraw the view
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setBeePosition(x, y)
                return true // Consume the touch event
            }
            MotionEvent.ACTION_MOVE -> {
                setBeePosition(x, y)
                return true // Consume the touch event
            }
            MotionEvent.ACTION_UP -> {
                return false
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        beeBitmap?.let {
            canvas.drawBitmap(it, beeX - it.width / 2, beeY - it.height / 2, null)
        }
    }
}