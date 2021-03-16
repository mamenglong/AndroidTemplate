package com.mml.template.net.cookie

import okhttp3.Cookie

fun Cookie.newBuilder(): Cookie.Builder {
    val builder = Cookie.Builder()
        .name(this.name)
        .value(this.value)
        .expiresAt(this.expiresAt)
        .path(path)

    if (this.hostOnly) {
        builder.hostOnlyDomain(this.domain).httpOnly()
    } else {
        builder.domain(this.domain)
    }

    if (this.secure) builder.secure()
    return builder
}