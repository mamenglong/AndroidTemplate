package com.maibaapp.sweetly.net.api

import com.maibaapp.sweetly.bean.RespondData
import com.maibaapp.sweetly.manager.UserManager
import com.maibaapp.sweetly.util.Result
import com.maibaapp.sweetly.util.no
import com.maibaapp.sweetly.util.yes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/11 16:39
 * Description: This is RetrofitCallback
 * Package: com.withu.find.net.api
 * Project: With U
 */

fun<T> registerRetrofitCallback(block: RetrofitCallback<T>.()->Unit) =
    RetrofitCallback<T>().also(block)

class RetrofitCallback<T> :Callback<RespondData<T>>{
    private var result: Result<Pair<T, String>, Throwable> = Result()
    fun callback(block: Result<Pair<T, String>, Throwable>.()->Unit){
        result= Result<Pair<T,String>,Throwable>().also(block)
    }
    private var onComplete:()->Unit = {}

    fun onComplete(block:()->Unit){
        onComplete =block
    }
    override fun onResponse(call: Call<RespondData<T>>, response: Response<RespondData<T>>) {
        response.isSuccessful.yes{
            val respondData = response.body()
            respondData?.let{
                respondData.isSccuess.yes{
                    result.doSuccess(respondData.data to respondData.msg)
                }.no{
                    if (respondData.shouldLogin){
                        UserManager.otherLogin()
                    }
                    result.doFailure(Throwable("code:${respondData.code} msg:${respondData.msg}"))
                }
            }?: kotlin.run{
                result.doFailure(Throwable("请求成功，数据为null"))
            }
        }.no{
            result.doFailure(Throwable("请求失败"))
        }
        onComplete.invoke()
    }

    override fun onFailure(call: Call<RespondData<T>>, t: Throwable) {
        result.doFailure(t)
        onComplete.invoke()
    }

}