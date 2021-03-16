package com.maibaapp.base.config

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Description: This is MMKV
 */
object MMKV {
    const val isShowAgreeDialog = "isShowAgreeDialog"
    fun init(application: Application){
        val rootDir: String = MMKV.initialize(application)
       // println("mmkv root: $rootDir")
    }
    fun default(): MMKV = MMKV.defaultMMKV()
    fun mmkvWithID(string: String,isMulti:Boolean = false):MMKV{
       return if (isMulti) {
           MMKV.mmkvWithID(string,MMKV.MULTI_PROCESS_MODE)
       }else{
           MMKV.mmkvWithID(string)
       }
    }
}