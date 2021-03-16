package com.mml.template

import com.mml.base.App
import dagger.hilt.android.HiltAndroidApp


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/27 19:07
 * Description: This is App
 */
@HiltAndroidApp
class App : App() {
    override val UMENG_APP_KEY: String = ConstString.UMENG_APP_KEY
    override fun lazyInitLibs() {
        super.lazyInitLibs()
    }

    override fun initLibAfterPermission() {
        super.initLibAfterPermission()

    }
}