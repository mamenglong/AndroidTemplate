package com.mml.template.util

import android.os.Build
import java.lang.reflect.Method

internal fun Class<*>.getDeclaredMethodDeeply(
    methodName: String,
    throwables: MutableList<Throwable>?,
    vararg parameterTypes: Class<*>
): Method? = try {
    this.getDeclaredMethod(methodName, *parameterTypes)
} catch (tr: Throwable) {
    throwables?.add(tr)
    this.superclass?.getDeclaredMethodDeeply(methodName, throwables, *parameterTypes)
}

internal fun buildParameterTypesString(vararg parameterTypes: Class<*>): String {
    if (parameterTypes.isEmpty()) {
        return "()"
    }
    val sb = StringBuilder()
    for (parameterType in parameterTypes) {
        sb.append(",").append(parameterType.name)
    }
    return sb.substring(1)
}

@Throws(NoSuchMethodException::class, SecurityException::class)
fun Class<*>.getDeclaredMethodDeeply(
    methodName: String,
    vararg parameterTypes: Class<*>
): Method {
    val throwables = mutableListOf<Throwable>()
    val method = this.getDeclaredMethodDeeply(methodName, throwables, *parameterTypes)
    when {
        method != null -> {
            return method
        }
        else -> {
            val ex =
                NoSuchMethodException("Can not found method: ${this.name}.${methodName}${buildParameterTypesString(*parameterTypes)}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (throwables.isNotEmpty()) {
                    for (tr in throwables) {
                        ex.addSuppressed(tr)
                    }
                }
            }
            throw ex
        }
    }
}

fun Class<*>.getDeclaredMethodDeeplyAndSafely(
    methodName: String,
    vararg parameterTypes: Class<*>
): Method? {
    return this.getDeclaredMethodDeeply(methodName, null, * parameterTypes)
}

