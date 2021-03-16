package com.maibaapp.sweetly.net.concurrent

interface IExecutable<T> {
    fun execute(): T
}