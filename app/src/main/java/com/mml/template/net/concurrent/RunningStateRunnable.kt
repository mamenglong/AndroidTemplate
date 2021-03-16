package com.mml.template.net.concurrent

abstract class RunningStateRunnable : FutureRunnable(), IRunningState,
    IExecutable<Void?> {
    @get:Synchronized
    @set:Synchronized
    override var isRunning = false

    override fun run() {
        isRunning = true
        execute()
        isRunning = false
    }
}