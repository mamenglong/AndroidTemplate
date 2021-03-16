package com.mml.template.net.api

/**
 * 接口管理
 * 可食用挂起函数
 */

object HttpManager{
    object WidgetWork{
        @JvmStatic
        suspend fun get(type:String="label",typeId:String="1")= RetrofitBuilder.apiService<WidgetWorkService>()
            .get(type,typeId)
        @JvmStatic
        @JvmOverloads
        fun get1(type:String="label", typeId:String="1")= RetrofitBuilder.apiService<WidgetWorkService>()
            .get1(type,typeId)
    }

}