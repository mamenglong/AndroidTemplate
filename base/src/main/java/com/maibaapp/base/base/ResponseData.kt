package com.maibaapp.base.base


data class ResponseData<out T>(val code: Int, val msg: String, val data: T?){
    val isSccuess: Boolean
        get() = code == 0
}