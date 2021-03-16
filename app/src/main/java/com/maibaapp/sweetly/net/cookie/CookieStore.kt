package com.maibaapp.sweetly.net.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieStore {
    fun add(uri: HttpUrl, cookies: List<Cookie>)
    operator fun get(uri: HttpUrl): List<Cookie>
    fun remove(uri: HttpUrl, cookie: Cookie?): Boolean
}