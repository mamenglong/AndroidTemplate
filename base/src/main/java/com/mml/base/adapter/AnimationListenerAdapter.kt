package com.mml.base.adapter

import android.view.animation.Animation

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 */
/**
 *  Animation 动画监听适配器 dsl
 */
fun animationListenerAdapter(adapter: AnimationListenerAdapter.()->Unit): Animation.AnimationListener {
    return AnimationListenerAdapter().apply(adapter)
}
class AnimationListenerAdapter:Animation.AnimationListener {
    private var animationRepeat:(Animation?)->Unit ={}
    private var animationEnd:(Animation?)->Unit ={}
    private var animationStart:(Animation?)->Unit ={}
    fun onAnimationRepeat(animationRepeat:(Animation?)->Unit){
        this.animationRepeat = animationRepeat
    }
    fun onAnimationEnd(animationEnd:(Animation?)->Unit){
        this.animationEnd = animationEnd
    }
    fun onAnimationStart(animationStart:(Animation?)->Unit){
        this.animationStart = animationStart
    }
    override fun onAnimationRepeat(animation: Animation?) {
        animationRepeat.invoke(animation)
    }

    override fun onAnimationEnd(animation: Animation?) {
       animationEnd.invoke(animation)
    }

    override fun onAnimationStart(animation: Animation?) {
        animationStart.invoke(animation)
    }

}