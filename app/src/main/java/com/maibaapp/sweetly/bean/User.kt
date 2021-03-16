package com.maibaapp.sweetly.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class User(
    @Keep var nickname:String="立即登陆",
    /**
     *  * 用户性别，0：未知/保密，1：女，2：男
     */
    var memo:String="",
    /**
     * 混淆后的uid（string）
     */
    var uid:String="",
    var phone:String="",

): Parcelable {

}