package com.mml.template.ui

import android.Manifest
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.mml.template.ConstString
import com.mml.base.EventBusKey
import com.mml.template.R
import com.mml.template.databinding.ActivitySplashBinding
import com.permissionx.guolindev.PermissionX

class SplashActivity : com.mml.base.base.BaseViewBindActivity<ActivitySplashBinding>() {
    override val dialogMsg: String
        get() = ConstString.USER_PRIVACY_MESSAGE.format(getString(R.string.app_name))
    override val isCanShowAgreeDialog: Boolean
        get() = true
    private val permissions = mutableListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
    )
    override val agreementDialogClickCallback: com.mml.base.base.AgreementDialogClickCallback?
        get() = object : com.mml.base.base.AgreementDialogClickCallback {
            override fun onNegativeButtonClick(dialog: DialogInterface) {
                super.onNegativeButtonClick(dialog)
                finish()
            }

            override fun onPositiveButtonClick(dialog: DialogInterface) {
                super.onPositiveButtonClick(dialog)
                com.mml.base.event.EventBus.postDelay<Boolean>(EventBusKey.INIT_LIBS, true)
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
        if (!com.mml.base.config.MMKV.default().getBoolean(com.mml.base.config.MMKV.isShowAgreeDialog, true)) {
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
        com.mml.base.event.EventBus.postDelay<Boolean>(EventBusKey.INIT_LIBS_AFTER_PERMISSION, true)
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
                        "${getString(R.string.app_name)} ??????????????????????????????,???????????????????????????"
                    else "${getString(R.string.app_name)} ?????????????????????????????????????????????????????????"
                scope.showRequestReasonDialog(deniedList, message, "??????", "??????")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "???????????????????????????????????????????????????????????????", "??????", "??????")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    gotoNext(true)
                    showDebugToast("allGranted:$allGranted")
                } else {
                    AlertDialog.Builder(this).apply {
                        setTitle("???????????????")
                        setMessage("???????????????????????????????????????????????????????????????????????????????????????")
                        setCancelable(false)
                        setPositiveButton("?????????") { _, _ ->
                            checkPermissions()
                        }
                        setNegativeButton("????????????") { _, _ ->
                            gotoNext()
                        }
                    }.show()
                }
            }
    }

}