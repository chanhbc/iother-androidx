package com.chanhbc.iother

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

@Suppress("unused")
open class SwipeViewPager(
    context: Context,
    attrs: AttributeSet?
) : ViewPager(context, attrs) {

    private var swipeEnabled: Boolean = true

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeViewPager)
        try {
            this.swipeEnabled = a.getBoolean(R.styleable.SwipeViewPager_swipe_enable, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setSwipeEnabled(enabled: Boolean) {
        this.swipeEnabled = enabled
    }
}