package com.mml.template

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/27 19:11
 * Description: This is ConstString
 * Package: com.withu.find
 * Project: With U
 */
object ConstString {
    const val ACTION_DEBUG = "android.intent.action.Debug"
    /**
     * 只有dev分支或debug模式为测试服链接,其他分支均为正式服
     */
    val IS_ONLINE = !BuildConfig.DEV && !BuildConfig.DEBUG

    object AppKey {
        // 阿里云轻 API
        const val ALIYUN_API_HOST = "json.api.bind.mml.net"
        const val ALIYUN_API_APP_KEY = "203806847"
        const val ALIYUN_API_APP_SECRET = "l79hq3n9dipkfkbq4cicu2wcrzneopu1"

        const val ALI_FEEDBACK_APPKEY = "31384289"
        const val ALI_FEEDBACK_SECRET = "8ce86c1d150f4f452e51639c7b1206e8"
    }
    const val UMENG_APP_KEY = "605012986ee47d382b8392f5"
    var USER_PRIVACY_MESSAGE: String =
        "\r\r欢迎使用[%s]。使用此软件之前请务必仔细阅读并理解《用户协议》和《隐私协议》，一经点击同意按钮则代表你已经知悉并同意协议内容,超出协议范围所产生的任何后果由你自行承担。"

    // QQ 包名
    const val QQ_PACKAGE_NAME = "com.tencent.mobileqq"
    const val WX_PACKAGE_NAME = "com.tencent.mm"

    object URL {
        /**
         * 使用引导
         */
        val USE_HELP: String = "http:www.baidu.com"

        //技术支持:
        const val TECHNICAL_SUPPORT =
            "http:www.baidu.com"

        //使用条款 用户协议
        const val TERMS_OF_USE =
            "http:www.baidu.com"

        //用户隐私协议
        const val USER_PRIVACY_AGREEMENT =
            "http:www.baidu.com"

        // 会员协议
        const val MEMBERSHIP_AGREEMENT =
            "http:www.baidu.com"

        //自动续费协议
        const val AUTOMATIC_RENEWAL_AGREEMENT =
            "http:www.baidu.com"
        const val ALI_OSS_IMG_BASE_URL = "https://elf-deco.img.maibaapp.com/"
        internal const val REQUEST_SECRET_KEY = "f1j6302leadv6mcxiftdzppvazoaotfj"

        internal const val MAX_DATE = 253402300799999L
        private const val ON_LINE_URL = "https://api.findu.ink/withu/"
        private const val TEST_URL = "http://58.33.45.170:18110/withu/"
        val BASE_SPARE_URL =
            if (IS_ONLINE) ON_LINE_URL else TEST_URL
    }
}