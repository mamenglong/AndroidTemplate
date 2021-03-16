package com.maibaapp.base.base


import androidx.viewbinding.ViewBinding


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 */
abstract class BaseViewBindActivity<T:ViewBinding> : BaseActivity(){

    protected lateinit var activityBinding: T

    abstract fun getViewBinding(): T

    override fun setContentView() {
        activityBinding= getViewBinding()
        setContentView(activityBinding.root)
    }

}