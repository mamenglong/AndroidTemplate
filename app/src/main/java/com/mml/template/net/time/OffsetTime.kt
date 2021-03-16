package com.mml.template.net.time

import com.mml.template.net.time.TimeService.elapsedRealTimeMillis

internal class OffsetTime(val type: Int, private val mSeed: Long, private val mOffset: Long) {
    fun currentTimeMillis(): Long {
        return mSeed + (elapsedRealTimeMillis() - mOffset)
    }

    override fun toString(): String {
        return "type: " + type + ", seed: " + mSeed + ", offset: " + mOffset + ", current: " + currentTimeMillis()
    }

    companion object {
        const val TYPE_NTP = 1
        const val TYPE_OUR_SERVER = 2
    }

}