package com.mml.template.net.concurrent

interface IExecutable<T> {
    fun execute(): T
}