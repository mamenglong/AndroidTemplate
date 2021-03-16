package com.mml.template.net.ssl

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

object AlwaysTrustHostnameVerifier : HostnameVerifier {

    override fun verify(hostname: String, session: SSLSession): Boolean {
        return true
    }
}