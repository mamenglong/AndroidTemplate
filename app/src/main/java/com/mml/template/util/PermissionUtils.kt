package com.mml.template.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.mml.base.App


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 1/7/21 7:55 PM
 * Description: This is PermissionUtils
 * Package: com.withu.find.util
 * Project: With U
 */
object PermissionUtils {
    /**
     * 上报位置时附带的通知权限开启状态信息，0:未开启 1:已开启
     */
    fun getNotifyPermissionStatus(context: Context): Int {
        return if (isNotificationPermissionGranted(context)) 1 else 0
    }

    /**
     * 检测『通知权限』是否被授权
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return NotificationManagerCompat.from(context.applicationContext).areNotificationsEnabled()
    }


    /**
     * 检查权限是否授予
     * @param context 上下文
     * @param permission 需要检查的权限
     * @return 是否授予该权限
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (context.checkPermission(
                permission,
                Process.myPid(),
                Process.myUid()
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    /**
     * 检查一组权限是否授予
     * @param context 上下文
     * @param permissions 需要检查的权限组
     * @return 未授予的权限
     */
    fun isPermissionGranted(context: Context, vararg permissions: String): Boolean =
        if (permissions.isEmpty()) true else permissions.map { isPermissionGranted(context, it) }
            .reduce { a, b -> a and b }


    /**
     * 自启动
     */
    fun jumpToSelfStartingPermissionPage(){
        gotoDetailPage(App.application)
    }
    /**
     * 跳转到应用信息页
     */
    private fun gotoDetailPage(context: Context) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    /**
     * 跳转到系统设置页
     */
    private fun gotoSettingsPage(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        context.startActivity(intent)
    }

    /**
     * 忽略电池优化
     */
    fun ignoreBatteryOptimization(context: Context) {
        val hasIgnored = isIgnoringBatteryOptimizations(context)
        //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
        if (!hasIgnored) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    }

    /**
     * 是都开启了电池优化
     */
    fun isIgnoringBatteryOptimizations(context: Context):Boolean{
       val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
       val hasIgnored = powerManager!!.isIgnoringBatteryOptimizations(context.packageName)
       return hasIgnored
   }

}