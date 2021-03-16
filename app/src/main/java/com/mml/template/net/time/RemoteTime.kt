package com.mml.template.net.time

import com.mml.template.net.concurrent.ConcurrentReference
import com.mml.template.net.time.TimeService.elapsedRealTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs

internal class RemoteTime {

    companion object {
        private val WRONG_SYSTEM_TIME_MIN_OFFSET = MillisTime.fromHours(1)
        private val CHECK_SERVER_TIME_DURATION = MillisTime.fromMinutes(10)
        private val NTP_SERVERS = arrayOf(
            "cn.ntp.org.cn", // 国家授时中心 NTP 服务器
            "ntp.ntsc.ac.cn", // 中国 NTP 快速授时服务
            "time.pool.aliyun.com", // 阿里云公共 NTP 服务器
            "time1.cloud.tencent.com", // 腾讯云公共 NTP 服务器
            "time3.aliyun.com", // 阿里云公共 NTP 服务器
        )
    }

    private val mServerTime = ConcurrentReference<OffsetTime>()
    private var mLastCheckTime = Long.MIN_VALUE

    init {
        checkAndUpdateServerTime()
    }

    fun currentTimeMillis(): Long {
        val offsetTime = mServerTime.get()
        this.console.log { "currentTimeMillis: {offsetTime: ${offsetTime?.currentTimeMillis()}) system: ${System.currentTimeMillis()}}" }
        return if (offsetTime == null) {
            checkAndUpdateServerTime() // 检查服务器时间
            System.currentTimeMillis()
        } else {
            offsetTime.currentTimeMillis()
        }
    }

    val isSystemTimeCorrect: Boolean
        get() = abs(currentTimeMillis() - System.currentTimeMillis()) < WRONG_SYSTEM_TIME_MIN_OFFSET

    internal fun checkAndUpdateServerTime() {
        this.console.log { "start [checkAndUpdateServerTime] ..." }
        this.console.log { "start [checkAndUpdateServerTime] ..." }
        if (mServerTime.notNull) {
            this.console.log { "complete [checkAndUpdateServerTime]: Server Time is settled" }
            // already prepared
            return
        }
        val time = elapsedRealTimeMillis()
        synchronized(this) {
            if (abs(time - mLastCheckTime) < CHECK_SERVER_TIME_DURATION) {
                this.console.log { "fail [checkAndUpdateServerTime]: Next update time is not reach" }
                return
            }
            mLastCheckTime = time
        }

        NTP_SERVERS.forEach { fetchServerTime(it) }
        this.console.log { "complete [checkAndUpdateServerTime]: All update tasks have executed" }
    }

    private fun fetchServerTime(ntpHost: String) = GlobalScope.launch(Dispatchers.IO) {
        var offsetTime: OffsetTime? = null
        try {
            offsetTime = SntpUtils.requestTime(ntpHost)
        } catch (ignored: Exception) {
        }
        if (offsetTime != null) {
            val setResult = mServerTime.conditionalSet(offsetTime) {
                it == null || it.type == OffsetTime.TYPE_OUR_SERVER
            }
            if (setResult) {
                this.console.log { arrayOf("set server time with: ", offsetTime) }
            }
        }
    }

}