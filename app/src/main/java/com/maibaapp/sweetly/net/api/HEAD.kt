package com.maibaapp.sweetly.net.api

internal interface HEADER {
    companion object {
        const val AUTHORIZATION = "Authorization"
        const val CACHE_CONTROL = "Cache-Control"
        const val CONNECTION = "Connection"
        const val KEEP_ALIVE = "keep-alive"
        const val NO_CACHE = "no-cache"
        const val PRAGMA = "Pragma"
        const val CONTENT_TYPE = "Content-Type"
        const val USER_AGENT = "User-Agent"
        const val E_TAG = "Etag"
        const val CONTENT_ENCODING = "Content-Encoding"
        const val SET_COOKIE = "Set-Cookie"
        const val SET_COOKIE2 = "Set-Cookie2"
        const val COOKIE = "Cookie"
        const val REFERER = "Referer"
    }
}