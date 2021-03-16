package com.maibaapp.base.base

import android.content.DialogInterface

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/13 09:47
 * Description: This is AgreementDialogClickCallback
 * Project: With U
 */
interface AgreementDialogClickCallback {
    fun onUserAgreementClick(){}
    fun onPrivacyAgreementClick(){}
    fun onDismiss(){}
    fun onShow(){}
    fun onNegativeButtonClick(dialog: DialogInterface) {}
    fun onPositiveButtonClick(dialog: DialogInterface) {}
}
