package com.mml.template.net.cookie

import com.tencent.mmkv.MMKV
import okhttp3.Cookie
import okhttp3.HttpUrl

object TikteamPrivateCookieStore : CookieStore {

    private const val SHARED_DOMAIN = "spare.mml.com" // 初代 Host, 作为共享 cookie 的 key

    private val mStore = MMKV.mmkvWithID( "tikteam_http_cookies")

    override fun add(uri: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach {
            mStore.putString(it.name, CookieBean.fromCookie(it).toJSONString())
        }
    }

    override fun get(uri: HttpUrl): List<Cookie> {
        val config = mStore
        val keys = config.allKeys()
        if (keys.isEmpty()) {
            return emptyList()
        }
        val cookies: MutableList<Cookie> = mutableListOf()

        for (key in keys) {
            val str = config.getString(key, null) ?: continue
            val bean = CookieBean.fromJson(str)
            if (bean != null) {
                cookies.add(bean.toCookie().newBuilder().domain(uri.host).build())
            }
        }

        return cookies
    }

    override fun remove(uri: HttpUrl, cookie: Cookie?): Boolean {
        mStore.clearAll()
        return true
    }
}