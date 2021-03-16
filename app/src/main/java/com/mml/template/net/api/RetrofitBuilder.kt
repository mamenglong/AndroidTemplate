package com.mml.template.net.api

import com.mml.template.ConstString
import okhttp3.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/12 11:36
 * Description: This is ApiCreate
 * 构造基础
 * Package: com.mml.module.main.net.retrofit2
 * Project: kassandra
 */
@Singleton
class RetrofitBuilder {
    @Inject
    lateinit var mSignerCallFactory: Call.Factory
    fun getRetrofit(baseUrl: String, isJsonConverter: Boolean = true): Retrofit {
        return retrofitMap[baseUrl] ?: kotlin.run {
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(mSignerCallFactory)
            //.client(mClient)
            if (isJsonConverter)
                builder.addConverterFactory(GsonConverterFactory.create())
            else
                builder.addConverterFactory(ScalarsConverterFactory.create())
            val result = builder.build()
            retrofitMap[baseUrl] = result
            result
        }
    }

    companion object {
        val map = mutableMapOf<Class<*>, IApiService>()
        private val retrofitMap = mutableMapOf<String, Retrofit>()
        val retrofitBuilder = RetrofitBuilder()
        inline fun <reified T : IApiService> apiService(
            baseUrl: String = ConstString.URL.BASE_SPARE_URL,
            isJsonConverter: Boolean = true
        ): T {
            return (map[T::class.java] as T?) ?: kotlin.run {
                val service =
                    retrofitBuilder.getRetrofit(baseUrl, isJsonConverter).create(T::class.java)
                map[T::class.java] = service
                service
            }
        }
    }
}