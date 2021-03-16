package com.maibaapp.sweetly.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.icu.text.DateFormat
import android.net.Uri
import android.text.TextUtils
import com.maibaapp.sweetly.App
import com.maibaapp.sweetly.ConstString.QQ_PACKAGE_NAME
import okhttp3.internal.and
import org.tikteam.commons.codec.binary.Base32
import org.tikteam.commons.codec.binary.Base64
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    val secureRandom: Random by lazy { SecureRandom() }

    val base32: Base32 by lazy { Base32(false) }
    val base64: Base64 by lazy { Base64() }

    val clipboard: ClipboardManager by lazy {
        App.application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    fun safeHeaderValue(value: String): String {
        if (TextUtils.isEmpty(value)) {
            return " "
        }
        val sb = java.lang.StringBuilder()
        var i = 0
        val length = value.length
        while (i < length) {
            val c = value[i]
            if (c <= '\u001f' && c != '\t' || c >= '\u007f') {
                sb.append('?')
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    fun sha1(data: String): String {
        return bytesToHex(sha1Bytes(data))
    }


    /**
     * 取SHA1
     * @param data 数据
     * @return 对应的hash值
     */
    private fun sha1Bytes(data: String): ByteArray {
        return try {
            val messageDigest =
                MessageDigest.getInstance("SHA1")
            messageDigest.reset()
            messageDigest.update(data.toByteArray(charset("UTF-8")))
            messageDigest.digest()
        } catch (e: Exception) {
            "".toByteArray()
        }
    }

    /**
     * 转16进制字符串
     * @param data 数据
     * @return 16进制字符串
     */
    private fun bytesToHex(data: ByteArray): String {
        val sb = StringBuilder()
        var stmp: String
        for (n in data.indices) {
            stmp = Integer.toHexString(data[n] and 0xff)
            if (stmp.length == 1) sb.append("0")
            sb.append(stmp)
        }
        return sb.toString().toUpperCase(Locale.CHINA)
    }

    fun openAppStore(context: Context, packageName: String? = null): Boolean {
        val pkName = packageName ?: context.packageName
        var result = context.startActivitySafely(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$pkName")
            )
        )
        if (!result) {
            result = context.startActivitySafely(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkName")
                )
            )
        }
        return result
    }

    fun openQQUri(context: Context, qqUri: String): Boolean {
        return context.startActivitySafely(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(qqUri)
            ).setPackage(QQ_PACKAGE_NAME)
        )
    }

    fun openQQ(context: Context, qqNumber: String): Boolean {
        val uri =
            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=${qqNumber}&card_type=person&source=qrcode"
        return openQQUri(context, uri)
    }

    fun openQQGroup(context: Context, qqGroupNumber: String): Boolean {
        val uri =
            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=${qqGroupNumber}&card_type=group&source=qrcode"
        return openQQUri(context, uri)
    }

    fun openBrowser(context: Context, url: String): Boolean {
        return context.startActivitySafely(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun openApp(context: Context, packageName: String): Boolean {
        val packageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
            ?: return false

        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        context.startActivity(intent)
        return true
    }

    fun dialTo(context: Context, numberUri: String): Boolean {
        return context.startActivitySafely(
            Intent().apply {
                action = "android.intent.action.DIAL"
                data = Uri.parse(numberUri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun emailTo(context: Context, emailUri: String): Boolean {
        return context.startActivitySafely(
            Intent.createChooser(
                Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse(emailUri) },
                "请选择要打开的应用"
            )
        )
    }

    private var sdf: SimpleDateFormat? = null
    fun formatUTC(l: Long, strPattern: String?): String {
        var strPattern = strPattern
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss"
        }
        if (sdf == null) {
            try {
                sdf = SimpleDateFormat(strPattern, Locale.CHINA)
            } catch (e: Throwable) {
            }
        } else {
            sdf!!.applyPattern(strPattern)
        }
        return if (sdf == null) "NULL" else sdf!!.format(l)
    }

    /**
     * 获取app的名称
     * @param context
     * @return
     */
    fun getAppName(context: Context): String? {
        var appName = ""
        try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(
                context.packageName, 0
            )
            val labelRes = packageInfo.applicationInfo.labelRes
            appName = context.resources.getString(labelRes)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return appName
    }


    /**
     * 分享文本
     */
    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivitySafely(Intent.createChooser(intent, "选择分享途径"))
    }

    fun copyText(context: Context, msg: String, toast: String) {
        clipboard.apply {
            val clip = ClipData.newPlainText(
                "text",
                msg
            )
            // Set the clipboard's primary clip.
            setPrimaryClip(clip)
            context.showToast(toast)
        }
    }

    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray? {
        val output = ByteArrayOutputStream()
        bmp.compress(CompressFormat.PNG, 100, output)
        if (needRecycle) {
            bmp.recycle()
        }
        val result = output.toByteArray()
        try {
            output.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }
}

object DateUtil {
    fun getCurrentDate(): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        return fmt.format(Date())
    }

    fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
    }

    /**
     * 时间戳转日期
     * 2014-1-23
     */
    fun getDate(long: Long): String {
        return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE)
            .format(Date(long))
    }

    /**
     * 时间戳转时间
     * 16:54:22
     */
    fun getTime(long: Long): String {
        return SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.SIMPLIFIED_CHINESE)
            .format(Date(long))
    }

}