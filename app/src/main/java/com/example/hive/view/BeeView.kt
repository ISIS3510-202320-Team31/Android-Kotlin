package com.example.hive.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.hive.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BeeView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var beeX: Float = 0f
    private var beeY: Float = 0f
    private var beeBitmap: Bitmap? = null

    init {
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.bee_icon)
        beeBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, false)
    }

    fun setBeePosition(x: Float, y: Float) {
        CoroutineScope(Dispatchers.Main).launch {
            beeX = x
            beeY = y
            invalidate() // Redraw the view
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                setBeePosition(x, y)
                return true
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
