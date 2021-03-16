package com.maibaapp.sweetly.ui

import android.Manifest
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.maibaapp.sweetly.ConstString
import com.maibaapp.sweetly.EventBusKey
import com.maibaapp.sweetly.R
import com.maibaapp.sweetly.base.base.AgreementDialogClickCallback
import com.maibaapp.sweetly.base.base.BaseViewBindActivity
import com.maibaapp.sweetly.base.config.MMKV
import com.maibaapp.sweetly.base.event.EventBus
import com.maibaapp.sweetly.databinding.ActivitySplashBinding
import com.permissionx.guolindev.PermissionX

class SplashActivity : BaseViewBindActivity<ActivitySplashBinding>() {
    override val isCanShowAgreeDialog: Boolean
        get() = true
    private val permissions = mutableListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
    )
    override val agreementDialogClickCallback: AgreementDialogClickCallback?
        get() = object : AgreementDialogClickCallback {
            override fun onNegativeButtonClick(dialog: DialogInterface) {
                super.onNegativeButtonClick(dialog)
                finish()
            }

            override fun onPositiveButtonClick(dialog: DialogInterface) {
                super.onPositiveButtonClick(dialog)
                EventBus.postDelay<Boolean>(EventBusKey.INIT_LIBS, true)
                checkPermissions()
            }

            override fun onPrivacyAgreementClick() {
                super.onPrivacyAgreementClick()
                CommonWebActivity.start(this@SplashActivity, ConstString.URL.USER_PRIVACY_AGREEMENT)
            }

            override fun onUserAgreementClick() {
                super.onUserAgreementClick()
                CommonWebActivity.start(this@SplashActivity, ConstString.URL.TERMS_OF_USE)
            }
        }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!MMKV.default().getBoolean(MMKV.isShowAgreeDialog, true)) {
            checkPermissions()
        }
    }

    private fun gotoNext(isDelay: Boolean = false) {
        if (isDelay) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(MainActivity::class.java)
                finish()
            }, 2000L)
        } else {
            startActivity(MainActivity::class.java)
            finish()
        }
        EventBus.postDelay<Boolean>(EventBusKey.INIT_LIBS_AFTER_PERMISSION, true)
    }

    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions(permissions)
            .explainReasonBeforeRequest()
            .setDialogTintColor(Color.parseColor("#008577"), Color.parseColor("#83e8dd"))
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                val message =
                    if (deniedList.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        "${getString(R.string.app_name)} 为保证程序的正常运行,请始终允许定位权限"
                    else "${getString(R.string.app_name)} 为保证程序的正常运行，请同意以下权限。"
                scope.showRequestReasonDialog(deniedList, message, "授权", "拒绝")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "以下权限被永久拒绝，请手动前往设置中心授权", "好的", "不了")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    gotoNext(true)
                    showDebugToast("allGranted:$allGranted")
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("权限被拒绝")
                        setMessage("有核心权限被拒绝，体验完整功能请保证应用已经获取所有权限。")
                        setCancelable(false)
                        setPositiveButton("去授权") { _, _ ->
                            checkPermissions()
                        }
                        setNegativeButton("继续使用") { _, _ ->
                            gotoNext()
                        }
                    }.show()
                }
            }
    }

}