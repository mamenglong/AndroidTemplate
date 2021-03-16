package com.maibaapp.base.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 */
abstract class BaseFragment :Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + CoroutineExceptionHandler(){ coroutineContext, throwable ->
            onCoroutineException(coroutineContext, throwable)
        }
    private lateinit var job: Job

    private var mRootView: View? = null
    open fun registerLiveEvent() {}
    open fun unRegisterLiveEvent() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView =inflater.inflate(getLayoutId(), container, false)
        mRootView?.isClickable = true// 防止点击穿透，底层的fragment响应上层点击触摸事件
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()
        initView()
        initData()
        startObserve()
    }
    override fun onDestroy() {
        super.onDestroy()
        unRegisterLiveEvent()
        job.cancel()
    }

    open fun startObserve() {
        registerLiveEvent()
    }
    /*需要实现方法区*/
    /**
     * @return LayoutId
     * @see [onCreateView]
     */
    open fun getLayoutId(): Int =0
    open fun initView(){}
    /**
     * 初始化数据  after [initView]
     */
    open fun initData(){}
    /**
     *    用于共享元素在调用[Activity#onActivityReenter回调]
     */
    open fun onReenter(data: Intent?) { // Do whatever with the data here
    }
    protected open fun showToast(msg: String){
        if (!isDetached)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    protected fun onCoroutineException(coroutineContext: CoroutineContext, throwable: Throwable){}

    // Fragment页面onResume函数重载
    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(this::class.java.simpleName) //统计页面("MainScreen"为页面名称，可自定义)
    }

    // Fragment页面onResume函数重载
    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(this::class.java.simpleName)
    }
}