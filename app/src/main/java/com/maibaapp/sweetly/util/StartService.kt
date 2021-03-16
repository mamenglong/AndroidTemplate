package com.maibaapp.sweetly.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.maibaapp.sweetly.net.time.console

fun <T : Context?> T.startServiceSafely(service: Intent): ComponentName? =
    if (this == null) {
        null
    } else try {
        this.startService(service)
    } catch (tr: Throwable) {
        console.err(tr)
        null
    }


fun <T : Context?> T.stopServiceSafely(service: Intent): Boolean =
    if (this == null) {
        false
    } else try {
        this.stopService(service)
    } catch (tr: Throwable) {
        console.err(tr)
        false
    }