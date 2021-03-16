package com.maibaapp.base.adapter

import android.widget.SeekBar

fun makeSeekProgressChangeListener(block: OnSeekProgressChangeListener.()->Unit): OnSeekProgressChangeListener {
    return OnSeekProgressChangeListener().apply(block)
}
class OnSeekProgressChangeListener : SeekBar.OnSeekBarChangeListener {
    private var onProgressChanged:(seekBar: SeekBar,
                                    progress: Int,
                                    fromUser: Boolean)->Unit={ seekBar: SeekBar, i: Int, b: Boolean -> } 
    private var onStartTrackingTouch:(seekBar: SeekBar)->Unit= {}
    private var onStopTrackingTouch:(seekBar: SeekBar)->Unit= {}
    
    fun onProgressChanged(block:(seekBar: SeekBar,
                          progress: Int,
                          fromUser: Boolean)->Unit){
        onProgressChanged = block
    }
    fun onStartTrackingTouch(block:(seekBar: SeekBar)->Unit){
        onStopTrackingTouch = block
    }
    fun onStopTrackingTouch(block:(seekBar: SeekBar)->Unit){
        onStopTrackingTouch = block
    }
    override fun onProgressChanged(
        seekBar: SeekBar,
        progress: Int,
        fromUser: Boolean
    ) {
        onProgressChanged.invoke(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        onStartTrackingTouch.invoke(seekBar)
    }
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        onStopTrackingTouch.invoke(seekBar)
    }
}