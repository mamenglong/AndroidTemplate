package com.maibaapp.base.adapter

import androidx.viewpager2.widget.ViewPager2

class ViewPager2OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {

    private var onPageScrollStateChanged:((Int)->Unit)? =null
    private var onPageScrolled:((Int,Float,Int)->Unit)? =null
    private var onPageSelected:((Int)->Unit)? =null
    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
        onPageScrollStateChanged?.invoke(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        onPageScrolled?.invoke(position,positionOffset,positionOffsetPixels)
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        onPageSelected?.invoke(position)
    }
    fun onPageSelected(block:(Int)->Unit){
        onPageSelected = block
    }
    fun onPageScrolled(block:(Int,Float,Int)->Unit){
        onPageScrolled = block
    }
    fun onPageScrollStateChanged(block:(Int)->Unit){
        onPageScrollStateChanged = block
    }
}

fun registerOnPageChangeCallback(block: ViewPager2OnPageChangeCallback.()->Unit) = ViewPager2OnPageChangeCallback().apply(block)