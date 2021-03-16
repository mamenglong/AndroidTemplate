package com.maibaapp.sweetly

import android.app.Application
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.maibaapp.sweetly.base.config.MMKV
import com.maibaapp.sweetly.base.event.EventBus
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.maibaapp.sweetly.log.LogUtil
import dagger.hilt.android.HiltAndroidApp


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/27 19:07
 * Description: This is App
 */
@HiltAndroidApp
class App : Application() {
    private var activityCount = 0

    init {
        application = this
    }

    companion object {
        lateinit var application: App
            private set
    }

    /**
     * 延迟初始化部分
     */
    private val observer:Observer<Boolean> = Observer<Boolean> {
        if (it) {
            initLibs(true)
        }
    }
    private val observerAfter:Observer<Boolean> = Observer<Boolean> {
        if (it) {
            initLibAfterPermission()
        }
    }
    override fun onCreate() {
        super.onCreate()
        LiveEventBus
            .config()
            .autoClear(true)
            .enableLogger(BuildConfig.DEBUG)
        MMKV.init(this)
        //同意隐私权限以后初始化
        if (!MMKV.default().getBoolean(MMKV.isShowAgreeDialog, true)) {
            initLibs(false)
        } else {
            EventBus.subscribeForever<Boolean>(EventBusKey.INIT_LIBS, observer)
        }
        EventBus.subscribeForever<Boolean>(EventBusKey.INIT_LIBS_AFTER_PERMISSION, observerAfter)
    }

    private fun initLibs(lazy: Boolean) {
        LogUtil.d("initLibs", this)
        initUmeng()
        initAliyun()
        if (lazy){
            EventBus.removeSubscriber<Boolean>(EventBusKey.INIT_LIBS, observer)
        }
    }
    private fun initLibAfterPermission(){
        EventBus.removeSubscriber<Boolean>(EventBusKey.INIT_LIBS_AFTER_PERMISSION, observerAfter)
    }
    private fun initAliyun(){
    /*    FeedbackAPI.init(
            application,
            ConstString.AppKey.ALI_FEEDBACK_APPKEY,
            ConstString.AppKey.ALI_FEEDBACK_SECRET
        )
        //设置默认联系方式
        //FeedbackAPI.setDefaultUserContactInfo("weixin")
        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true)
        //设置返回按钮图标
        FeedbackAPI.setBackIcon(R.drawable.ic_baseline_arrow_back_24)
        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
        FeedbackAPI.setHistoryTextSize(16f)
        //设置标题栏高度，单位为像素
        //FeedbackAPI.setTitleBarHeight(DisplayUtils.dp2px(45f))*/
    }
    private fun initUmeng() {
        /**
         * AnalyticsConfig.getChannel(this)
         *         <!--value的值填写渠道名称，例如yingyongbao。这里设置动态渠道名称变量-->
        <meta-data android:value="${UMENG_CHANNEL_VALUE}" android:name="UMENG_CHANNEL"/>
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey   如果Manifest文件中已配置app key，该参数可以传空，则使用Manifest中配置的app key，否则该参数必须传入
         * 参数3:【友盟+】 Channel 如果Manifest文件中已配置channel，该参数可以传空，则使用Manifest中配置的channel，否则该参数必须传入，channel命名请详见channel渠道命名规范
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
        UMConfigure.init(
            this,
            ConstString.UMENG_APP_KEY,
            null,
            UMConfigure.DEVICE_TYPE_PHONE,
            null
        )
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        /**
         * 设置日志加密
         * 参数：boolean 默认为false（不加密）
         */
        UMConfigure.setEncryptEnabled(true)
        /**
         * 子进程是否支持自定义事件统计。
         * 参数：boolean 默认不使用
         */
        UMConfigure.setProcessEvent(true)
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    override fun onTerminate() {
        super.onTerminate()
    }


    /**
     * 判断app是否在后台
     * @return
     */
    fun isBackground() = activityCount <= 0
}