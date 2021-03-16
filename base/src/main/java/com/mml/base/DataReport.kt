package com.mml.base

import android.content.Context
import com.umeng.analytics.AnalyticsConfig
import com.umeng.analytics.MobclickAgent


object DataReport {
    @JvmStatic
    fun getChannel(context: Context): String {
       return AnalyticsConfig.getChannel(context)?:"default"
    }
    @JvmStatic
    fun onEvent(context: Context, key: String){
        MobclickAgent.onEvent(context, key)
    }
    @JvmStatic
    fun onEvent(context: Context, key: String, map: Map<String, String>){
        MobclickAgent.onEvent(context, key, map)
    }
    @JvmStatic
    fun onEvent(key: String){
        MobclickAgent.onEvent(App.application, key)
    }
    @JvmStatic
    fun onEvent(key: String, vararg pair: Pair<String, String>){
        val map = mutableMapOf<String, String>()
        pair.forEach {
            map[it.first] = it.second
        }
        MobclickAgent.onEvent(App.application, key, map)
    }
}

