package com.mml.base.log

import android.util.Log

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/30 21:17
 * Description: This is LogUtil
 * Package: com.withu.find.log
 * Project: With U
 */
object LogUtil {
    /**
     * @param tag 类名,自定义tag请使用 [i]
     */
    fun d(msg:String,tag:Any){
        Log.d("${tag.javaClass.simpleName} ---->",msg)
    }
    fun i(msg:String,tag:Any){
        Log.d("$tag ---->",msg)
    }
}