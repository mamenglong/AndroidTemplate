package com.maibaapp.sweetly.net.time

import android.os.SystemClock
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeService {
    private val TIME_FORMAT: DateFormat =
        SimpleDateFormat("yyyyMMddHHmmssSSSZ", Locale.ROOT)
    private val HUMAN_READABLE_TIME_FORMAT: DateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ ", Locale.ROOT)
    private val HUMAN_READABLE_TIME_FILE_NAME_FORMAT: DateFormat =
        SimpleDateFormat("yyyy-MM-dd＠HH-mm-ss@SSSZ", Locale.ROOT)

    private val mRemoteTimeService = RemoteTime()

    fun startup() {
        mRemoteTimeService.checkAndUpdateServerTime()
    }

    /**
     * Returns the current time in **days**** since January 1, 1970 00:00:00.0 UTC.
     ** */
    fun currentTimeDays(zone: TimeZone? = null): Long {
        return TimeUnit.MILLISECONDS.toDays(
            currentTimeMillis(
                zone
            )
        )
    }

    /**
     * Returns the current time in **seconds**** since January 1, 1970 00:00:00.0 UTC.
     ** */
    fun currentTimeSeconds(zone: TimeZone? = null): Long {
        return TimeUnit.MILLISECONDS.toSeconds(
            currentTimeMillis(
                zone
            )
        )
    }

    /**
     * Returns the current time in **minutes**** since January 1, 1970 00:00:00.0 UTC.
     ** */
    fun currentTimeMinutes(zone: TimeZone? = null): Long {
        return TimeUnit.MILLISECONDS.toMinutes(
            currentTimeMillis(
                zone
            )
        )
    }

    /**
     * Returns the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     */
    fun currentTimeMillis() = mRemoteTimeService.currentTimeMillis()

    fun isSystemTimeCorrect() = mRemoteTimeService.isSystemTimeCorrect

    fun currentTimeMillis(zone: TimeZone?): Long {
        val time = currentTimeMillis()
        return if (zone == null) time else time + zone.rawOffset
    }

    /**
     * 开机以来的时间 (秒)
     */
    fun elapsedRealTimeSeconds(): Long {
        return TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime())
    }

    /**
     * 开机以来的时间 (毫秒)
     */
    @JvmStatic
    fun elapsedRealTimeMillis(): Long {
        return SystemClock.elapsedRealtime()
    }

    /**
     * 开机以来的时间 (纳秒)
     */
    fun elapsedRealTimeNanos(): Long {
        return SystemClock.elapsedRealtimeNanos()
    }

    @JvmOverloads
    fun createHumanReadableDateTimestampForFileName(time: Long = currentTimeMillis()): String {
        return createDateTimestamp(
            time,
            HUMAN_READABLE_TIME_FILE_NAME_FORMAT
        )
    }

    @JvmOverloads
    fun createHumanReadableDateTimestamp(time: Long = currentTimeMillis()): String {
        return createDateTimestamp(
            time,
            HUMAN_READABLE_TIME_FORMAT
        )
    }

    fun createDateTimestamp(): String {
        return createDateTimestamp(
            currentTimeMillis(),
            TIME_FORMAT
        )
    }

    fun createDateTimestamp(time: Long): String {
        return createDateTimestamp(
            time,
            TIME_FORMAT
        )
    }

    private fun createDateTimestamp(
        time: Long,
        format: DateFormat
    ): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return format.format(calendar.time)
    }

    val currentTimeStamp: Long
        get() = currentTimeMillis() / 1000

    fun stampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT) //这个是你要转成后的时间的格式
        return sdf.format(Date(timestamp))
    }

    fun covertToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.ROOT) //这个是你要转成后的时间的格式
        return sdf.format(Date(timestamp))
    }

    fun createDateFromPattern(timestamp: Long, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.ROOT)
        return sdf.format(Date(timestamp))
    }

    /**
     * 获取当前时间的明天凌晨时间时间戳
     *
     * @return
     */
    val secondsNextEarlyMorning: Long
        get() {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.timeInMillis
        }

    /**
     * 获取今年第一天的时间
     *
     * @return 今年第一天时间，单位毫秒
     */
    val yearStartTime: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            val currentYear = calendar[Calendar.YEAR]
            calendar.clear()
            calendar[Calendar.YEAR] = currentYear
            return calendar.timeInMillis
        }
}