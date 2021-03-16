package com.mml.template.net.ssl

import okhttp3.internal.tls.OkHostnameVerifier
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

class DefaultHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean {
        return OkHostnameVerifier.verify(hostname, session) ||
                HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)
    }
}