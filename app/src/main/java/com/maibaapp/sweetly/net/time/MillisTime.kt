package com.maibaapp.sweetly.net.time

import java.util.concurrent.TimeUnit

/**
 * 毫秒和各个时间单位之间的抓换类
 */
object MillisTime {
    /**
     * 一秒包含的毫秒数
     */
    val TIME_SECOND = fromSeconds(1)
    /**
     * 一分钟包含的毫秒数
     */
    val TIME_MINUTE = fromMinutes(1)
    /**
     * 一小时包含的毫秒数
     */
    val TIME_HOUR = fromHours(1)
    /**
     * 一天包含的毫秒数
     */
    val TIME_DAY = fromDays(1)

    /**
     * 把特定单位的时间转化成毫秒
     */
    fun fromMicros(time: Long): Long {
        return from(time, TimeUnit.MICROSECONDS)
    }

    fun fromNanos(time: Long): Long {
        return from(time, TimeUnit.NANOSECONDS)
    }

    fun fromSeconds(time: Long): Long {
        return from(time, TimeUnit.SECONDS)
    }

    fun fromMinutes(time: Long): Long {
        return from(time, TimeUnit.MINUTES)
    }

    fun fromHours(time: Long): Long {
        return from(time, TimeUnit.HOURS)
    }

    fun fromDays(time: Long): Long {
        return from(time, TimeUnit.DAYS)
    }

    fun from(time: Long, unit: TimeUnit): Long {
        return TimeUnit.MILLISECONDS.convert(time, unit)
    }

    /**
     * 把毫秒转化成特定单位的时间
     */
    fun toMicros(time: Long): Long {
        return to(time, TimeUnit.MICROSECONDS)
    }

    fun toNanos(time: Long): Long {
        return to(time, TimeUnit.NANOSECONDS)
    }

    fun toSeconds(time: Long): Long {
        return to(time, TimeUnit.SECONDS)
    }

    fun toMinutes(time: Long): Long {
        return to(time, TimeUnit.MINUTES)
    }

    fun toHours(time: Long): Long {
        return to(time, TimeUnit.HOURS)
    }

    fun toDays(time: Long): Long {
        return to(time, TimeUnit.DAYS)
    }

    fun to(time: Long, unit: TimeUnit): Long {
        return unit.convert(time, TimeUnit.MILLISECONDS)
    }
}