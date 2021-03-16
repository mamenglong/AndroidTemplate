package com.maibaapp.sweetly.base.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.maibaapp.sweetly.base.util.ViewUtil.dp
import com.maibaapp.sweetly.databinding.DialogLoadingBinding


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 */
abstract class BaseViewBindFragment<T:ViewBinding> : BaseFragment(){
    private lateinit var loadingDialog:AlertDialog
    protected lateinit var fragmentBinding: T
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentBinding = getViewBinding()
        fragmentBinding.root.isClickable = true// 防止点击穿透，底层的fragment响应上层点击触摸事件
        val loadingViewBinding = DialogLoadingBinding.inflate(layoutInflater)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(loadingViewBinding.root)
            .create()
        return fragmentBinding.root
    }
    abstract fun getViewBinding():T
    fun showLoading(){
        loadingDialog.show()
        loadingDialog.window?.attributes=loadingDialog.window?.attributes?.apply {
            width= 96f.dp.toInt()
            height = width
        }
    }
    fun hideLoading() {
        loadingDialog.hide()
    }

    override fun onDetach() {
        super.onDetach()
        loadingDialog.onDetachedFromWindow()
    }
}