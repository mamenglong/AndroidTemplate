package com.maibaapp.sweetly.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.maibaapp.sweetly.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/29 21:30
 * Description: This is Ext
 * Package: com.withu.find.util
 * Project: With U
 */

fun Context.showToast(msg:String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}
fun Fragment.showToast(msg: String){
    requireContext().showToast(msg)
}
fun Fragment.showDebugToast(msg:String){
    if (BuildConfig.DEBUG)
        showToast(msg)
}
fun Boolean.yes(block:()->Unit):Boolean{
    if (this){
        block()
    }
    return this
}
fun Boolean.no(block:()->Unit):Boolean{
    if (!this){
        block()
    }
    return this
}
suspend fun View.awaitNextLayout() = suspendCancellableCoroutine<Unit> { cont ->
    // 这里的 lambda 表达式会被立即调用，允许我们创建一个监听器
    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            v: View?,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int) {
            // 视图的下一次布局任务被调用
            // 先移除监听，防止协程泄漏
            v?.removeOnLayoutChangeListener(this)
            // 最终，唤醒协程，恢复执行
            cont.resume(Unit) {}
        }
    }
    // 如果协程被取消，移除该监听
    cont.invokeOnCancellation { removeOnLayoutChangeListener(listener) }
    // 最终，将监听添加到 view 上
    addOnLayoutChangeListener(listener)
    // 这样协程就被挂起了，除非监听器中的 cont.resume() 方法被调用

}

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->

    // 增加一个处理协程取消的监听器，如果协程被取消，
    // 同时执行动画监听器的 onAnimationCancel() 方法，取消动画
    cont.invokeOnCancellation { cancel() }

    addListener(object : AnimatorListenerAdapter() {
        private var endedSuccessfully = true

        override fun onAnimationCancel(animation: Animator) {
            // 动画已经被取消，修改是否成功结束的标志
            endedSuccessfully = false
        }

        override fun onAnimationEnd(animation: Animator) {

            // 为了在协程恢复后的不发生泄漏，需要确保移除监听
            animation.removeListener(this)
            if (cont.isActive) {

                // 如果协程仍处于活跃状态
                if (endedSuccessfully) {
                    // 并且动画正常结束，恢复协程
                    cont.resume(Unit){}
                } else {
                    // 否则动画被取消，同时取消协程
                    cont.cancel()
                }
            }
        }
    })
}