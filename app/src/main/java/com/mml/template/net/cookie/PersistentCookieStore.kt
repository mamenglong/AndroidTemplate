package com.mml.template.net.cookie


import com.mml.base.log.LogUtil
import com.tencent.mmkv.MMKV
import okhttp3.Cookie
import okhttp3.HttpUrl

class PersistentCookieStore(configName: String) : CookieStore {
    private var mStore = MMKV.mmkvWithID(configName)

    override fun add(uri: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach {
         LogUtil.d("host: ${uri.host} cookie.domain: ${it.domain}", "PersistentCookieStore")
            mStore.putString(it.name, CookieBean.fromCookie(it).toJSONString())
        }
    }

    override fun get(uri: HttpUrl): List<Cookie> {
        val keys = mStore.allKeys()
        if (keys.isNullOrEmpty()) {
            return emptyList()
        }
        val cookies: MutableList<Cookie> = mutableListOf()

        for (key in keys) {
            val str = mStore.getString(key.toString(), null) ?: continue
            val bean = CookieBean.fromJson(str)
            if (bean != null) {
                cookies.add(bean.toCookie())
            }
        }

        return cookies
    }

    override fun remove(uri: HttpUrl, cookie: Cookie?): Boolean {
        mStore.clearAll()
        return true
    }

}