package com.maibaapp.sweetly.net.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WidgetWorkService: IApiService {
    @GET("panda/countdown-v4/v4/list")
    suspend fun get(@Query("type") type:String = "label", @Query("typeId") typeId:String="1", @Query("pageNum") pageNum:String="1", @Query("pageSize")  pageSize:String="20" ) :Any
    @GET("panda/countdown-v4/v4/list")
    fun get1(@Query("type") type:String = "label", @Query("typeId") typeId:String="1",@Query("pageNum") pageNum:String="1", @Query("pageSize")  pageSize:String="20" ) : Call<Any>

}