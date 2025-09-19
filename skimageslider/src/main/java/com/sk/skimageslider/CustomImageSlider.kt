package com.sk.skimageslider

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class CustomImageSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager = ViewPager2(context)
    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollInterval: Long = 3000 // default 3 sec
    private var isAutoScrollEnabled = false
    private var isSwipeEnabled = true

    private var runnable: Runnable? = null

    init {
        addView(viewPager, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    fun setItems(
        items: List<SliderItem>,
        defaultErrorImage: Int = R.drawable.ic_broken, // fallback
        onItemClick: ((SliderItem) -> Unit)? = null
    ) {
        viewPager.adapter = SliderAdapter(items, defaultErrorImage, onItemClick)
        setupSwipe()
    }

    fun setAutoScroll(interval: Long, enabled: Boolean) {
        autoScrollInterval = interval
        isAutoScrollEnabled = enabled
        if (enabled) startAutoScroll() else stopAutoScroll()
    }

    fun setSwipeEnabled(enabled: Boolean) {
        isSwipeEnabled = enabled
        setupSwipe()
    }

    private fun setupSwipe() {
        try {
            val recyclerView = viewPager.getChildAt(0) as RecyclerView
            recyclerView.isNestedScrollingEnabled = isSwipeEnabled
            recyclerView.setOnTouchListener { _, _ -> !isSwipeEnabled }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAutoScroll() {
        stopAutoScroll()
        runnable = object : Runnable {
            override fun run() {
                val count = viewPager.adapter?.itemCount ?: 0
                if (count > 0) {
                    val nextItem = (viewPager.currentItem + 1) % count
                    viewPager.currentItem = nextItem
                }
                handler.postDelayed(this, autoScrollInterval)
            }
        }
        handler.postDelayed(runnable!!, autoScrollInterval)
    }

    private fun stopAutoScroll() {
        runnable?.let { handler.removeCallbacks(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAutoScroll()
    }
}

// Adapter

