package com.mml.template.net.cookie

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

class WebviewCookieJar : CookieJar {

    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>
    ) {
        val urlString = url.toString()
        val cookieMgr = CookieManager.getInstance()
        for (cookie in cookies) {
            cookieMgr.setCookie(urlString, cookie.toString())
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val urlString = url.toString()
        val cookiesString = CookieManager.getInstance().getCookie(urlString)
        if (cookiesString != null && cookiesString.isNotEmpty()) {
            val cookieHeaders = cookiesString.split(";".toRegex()).toTypedArray()
            val cookies: MutableList<Cookie> =
                ArrayList(cookieHeaders.size)
            for (header in cookieHeaders) {
                Cookie.parse(url, header)?.let { cookies.add(it) }
            }
            return cookies
        }
        return emptyList()
    }
}