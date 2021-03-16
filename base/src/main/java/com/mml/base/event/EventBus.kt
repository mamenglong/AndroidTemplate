package com.mml.base.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-12-31 上午9:58
 * Description: This is EventBus

 */
object EventBus {
    /**
     * 具有生命周期感知能力，LifecycleOwner销毁时自动取消订阅，不需要调用removeObserver
     */
    inline fun <reified T> subscribe(
        owner: LifecycleOwner,
        key: String,
        crossinline onChange: (T) -> Unit = {}
    ) {
        LiveEventBus
            .get(key, T::class.java)
            .observe(owner, Observer<T> {
                onChange.invoke(it)
            })
    }

    /**
     * Forever模式订阅消息，需要调用removeObserver取消订阅
     */
    inline fun <reified T> subscribeForever(key: String, crossinline onChange: (T) -> Unit = {}) {
        LiveEventBus
            .get(key, T::class.java)
            .observeForever(Observer<T> {
                onChange.invoke(it)
            })
    }
    /**
     * Forever模式订阅消息，需要调用removeObserver取消订阅
     */
    inline fun <reified T> subscribeForever(key: String, observer: Observer<T>) {
        LiveEventBus
            .get(key, T::class.java)
            .observeForever(observer)
    }
    fun <T> postDelay(key: String, value: T, delay: Long = 0) {
        LiveEventBus
            .get(key)
            .postDelay(value, delay)
    }

    /**
     *  removeObserver取消订阅
     */
    inline fun <reified T> removeSubscriber(key: String, observer: Observer<T>) {
        LiveEventBus
            .get(key, T::class.java)
            .removeObserver(observer)
    }

    /**
     * 在订阅消息的时候设置Sticky模式，这样订阅者可以接收到之前发送的消息
     * 具有生命周期感知能力，LifecycleOwner销毁时自动取消订阅，不需要调用removeObserver
     */
    inline fun <reified T> subscribeSticky(
        owner: LifecycleOwner,
        key: String,
        crossinline onChange: (T) -> Unit = {}
    ) {
        LiveEventBus
            .get(key, T::class.java)
            .observeSticky(owner, Observer<T> {
                onChange.invoke(it)
            })
    }

    /**
     * 在订阅消息的时候设置Sticky模式，这样订阅者可以接收到之前发送的消息
     * Forever模式订阅消息，需要调用removeObserver取消订阅
     */

    inline fun <reified T> subscribeStickyForever(
        key: String,
        crossinline onChange: (T) -> Unit = {}
    ) {
        LiveEventBus
            .get(key, T::class.java)
            .observeStickyForever(Observer<T> {
                onChange.invoke(it)
            })
    }
}