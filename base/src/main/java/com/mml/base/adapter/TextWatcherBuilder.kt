package com.mml.base.adapter

import android.text.Editable
import android.text.TextWatcher

private typealias BeforeTextChangedCallback =
            (s: CharSequence?, start: Int, count: Int, after: Int) -> Unit

private typealias OnTextChangedCallback =
            (s: CharSequence?, start: Int, before: Int, count: Int) -> Unit

private typealias AfterTextChangedCallback = (s: Editable?) -> Unit

class TextWatcherBuilder : TextWatcher {

    private var beforeTextChangedCallback: BeforeTextChangedCallback? = null
    private var onTextChangedCallback: OnTextChangedCallback? = null
    private var afterTextChangedCallback: AfterTextChangedCallback? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
        beforeTextChangedCallback?.invoke(s, start, count, after) ?: Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
        onTextChangedCallback?.invoke(s, start, before, count) ?: Unit

    override fun afterTextChanged(s: Editable?) =
        afterTextChangedCallback?.invoke(s) ?: Unit

    fun beforeTextChanged(callback: BeforeTextChangedCallback) {
        beforeTextChangedCallback = callback
    }

    fun onTextChanged(callback: OnTextChangedCallback) {
        onTextChangedCallback = callback
    }

    fun afterTextChanged(callback: AfterTextChangedCallback) {
        afterTextChangedCallback = callback
    }

}

fun registerTextWatcher(function: TextWatcherBuilder.() -> Unit) =
    TextWatcherBuilder().also(function)