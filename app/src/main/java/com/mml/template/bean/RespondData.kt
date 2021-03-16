package com.mml.template.bean

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/2 10:48
 * Description: This is RespondData
 * Package: com.withu.find.bean
 * Project: With U
 */
data class RespondData<T>(
    val code: Int,
    val msg: String,
    val data: T
) {
    val isSccuess: Boolean
        get() = code == 0
    val shouldLogin: Boolean
        get() = code == 1
}