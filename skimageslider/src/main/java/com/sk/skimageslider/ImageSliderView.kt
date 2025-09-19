package com.sk.skimageslider

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ImageSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager = ViewPager2(context)
    private val indicatorLayout = LinearLayout(context) // dots
    private val handler = Handler(Looper.getMainLooper())

    private var autoScrollInterval: Long = 3000
    private var isAutoScrollEnabled = false
    private var isSwipeEnabled = true

    private var activeDotColor: Int = Color.WHITE
    private var inactiveDotColor: Int = Color.GRAY
    private var cornerRadius: Float = 0f
    private var shadeEnabled: Boolean = true

    private var runnable: Runnable? = null

    init {
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(viewPager, params)

        indicatorLayout.orientation = LinearLayout.HORIZONTAL
        indicatorLayout.gravity = Gravity.CENTER
        val indicatorParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        indicatorParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        indicatorParams.setMargins(0, 0, 0, 32)
        addView(indicatorLayout, indicatorParams)
    }

    fun setItems(
        items: List<SliderItem>,
        defaultErrorImage: Int = R.drawable.ic_broken,
        onItemClick: ((SliderItem) -> Unit)? = null
    ) {
        viewPager.adapter = SliderAdapter(items, defaultErrorImage, onItemClick)
        setupIndicators(items.size)
        setupSwipe()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })
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

    fun setIndicatorColors(active: Int, inactive: Int) {
        activeDotColor = active
        inactiveDotColor = inactive
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        // Apply to ViewPager's children (CardView in item layout)
    }

    fun setShadeEnabled(enabled: Boolean) {
        shadeEnabled = enabled
        // handled in adapter (toggle background visibility)
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

    private fun setupIndicators(count: Int) {
        indicatorLayout.removeAllViews()
        for (i in 0 until count) {
            val dot = View(context)
            val size = 16
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(8, 0, 8, 0)
            dot.layoutParams = params
            dot.background = createDotDrawable(inactiveDotColor)
            indicatorLayout.addView(dot)
        }
        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            val dot = indicatorLayout.getChildAt(i)
            dot.background =
                if (i == position) createDotDrawable(activeDotColor)
                else createDotDrawable(inactiveDotColor)
        }
    }

    private fun createDotDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
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

