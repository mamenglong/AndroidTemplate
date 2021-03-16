package com.mml.template.net.cookie

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Cookie
internal const val MAX_DATE = 253402300799999L

class CookieBean {
    @SerializedName(value = "name")
    var name: String? = null
    @SerializedName(value = "value")
    var value: String? = null
    @SerializedName(value = "expiresAt")
    var expiresAt: Long = MAX_DATE
    @SerializedName(value = "domain")
    var domain: String? = null
    @SerializedName(value = "path")
    var path = "/"
    @SerializedName(value = "secure")
    var secure = false
    @SerializedName(value = "httpOnly")
    var httpOnly = false
    @SerializedName(value = "persistent")
    var persistent = false
    @SerializedName(value = "hostOnly")
    var hostOnly = false


    fun toCookie(): Cookie {
        var builder = Cookie.Builder().name(name!!)
        builder = builder.value(value!!)
        builder = builder.expiresAt(expiresAt)
        builder = if (hostOnly) builder.hostOnlyDomain(domain!!) else builder.domain(domain!!)
        builder = builder.path(path)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        return builder.build()
    }

    fun toJSONString(): String {
        return ADAPTER.toJson(this)
    }

    companion object {
        private val ADAPTER = Gson()

        fun fromJson(json: String): CookieBean? {
            try {
                return ADAPTER.fromJson(json, CookieBean::class.java)
            } catch (tr: Throwable) {
            }
            return null
        }

        fun fromCookie(cookie: Cookie): CookieBean {
            val cookieBean = CookieBean()
            cookieBean.name = cookie.name
            cookieBean.value = cookie.value
            cookieBean.expiresAt = cookie.expiresAt
            cookieBean.domain = cookie.domain
            cookieBean.path = cookie.path
            cookieBean.secure = cookie.secure
            cookieBean.httpOnly = cookie.httpOnly
            cookieBean.hostOnly = cookie.hostOnly
            cookieBean.persistent = cookie.persistent
            return cookieBean
        }
    }
}