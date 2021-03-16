package com.mml.template.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.mml.template.net.time.console

fun <T : Context?> T.startActivitySafely(
    @RequiresPermission intent: Intent?,
    options: Bundle? = null
) =
    if (this == null || intent == null) {
        false
    } else try {
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        this.startActivity(intent, options)
        true
    } catch (tr: Throwable) {
        console.err(tr)
        false
    }

fun <T : Activity?> T.startActivityForResultSafely(
    @RequiresPermission intent: Intent?, requestCode: Int,
    options: Bundle? = null
) =
    if (this == null || intent == null) {
        false
    } else try {
        this.startActivityForResult(intent, requestCode, options)
        true
    } catch (tr: Throwable) {
        console.err(tr)
        false
    }

