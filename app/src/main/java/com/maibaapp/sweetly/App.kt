package com.maibaapp.sweetly

import android.app.Application
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.maibaapp.base.App
import com.maibaapp.base.config.MMKV
import com.maibaapp.base.event.EventBus
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.maibaapp.base.log.LogUtil
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