package com.example.hive.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    // true if we can scroll the ScrollView
    // false if we cannot scroll
    private var scrollable = true

    fun setScrollingEnabled(scrollable: Boolean) {
        this.scrollable = scrollable
    }

    fun isScrollable(): Boolean {
        return scrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // if we can scroll, pass the event to the superclass
                if (scrollable) return super.onTouchEvent(ev)
                // only continue to handle the touch event if scrolling enabled
                return scrollable // scrollable is always false at this point
            }
            else -> return super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return if (!scrollable) false else super.onInterceptTouchEvent(ev)
    }
}