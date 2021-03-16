package com.maibaapp.sweetly.base.base


data class ResponseData<out T>(val code: Int, val msg: String, val data: T?)