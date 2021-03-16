package com.mml.template.util

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/29 20:18
 * Description: This is Result
 * Package: com.withu.find.util
 * Project: With U
 */
fun interface SUCCESS<T>{
    fun onSuccess(data:T)
}
fun interface FAILURE<T>{
    fun onFailure(data:T)
}
fun<S,F> result(block: Result<S, F>.()->Unit) = Result<S,F>().also (block)
class Result<S,F>{
    private var onSuccess: SUCCESS<S> = SUCCESS{}
    private var onFailure = FAILURE<F> {  }
    fun onSuccess(block: SUCCESS<S>){
         onSuccess = block
    }
    fun onFailure(block: FAILURE<F>){
        onFailure = block
    }
    fun doSuccess(data:S){
        onSuccess.onSuccess(data)
    }
    fun doFailure(exception:F){
        onFailure.onFailure(exception)
    }
}
