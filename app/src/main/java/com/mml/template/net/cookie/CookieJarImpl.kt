package com.mml.template.net.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieJarImpl(cookieStore: CookieStore) : CookieJar {
    private val mCookieStore: CookieStore = cookieStore
    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        mCookieStore.add(url, cookies)
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return mCookieStore[url] ?: emptyList()
    }

}