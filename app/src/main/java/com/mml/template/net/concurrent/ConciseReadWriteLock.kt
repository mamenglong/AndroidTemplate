package com.mml.template.net.concurrent

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class ConciseReadWriteLock {
    private val mLock: ReadWriteLock = ReentrantReadWriteLock()

    private val mReadLock = mLock.readLock()
    private val mWriteLock = mLock.writeLock()

    private val mReadKey: IKey = Key(mReadLock)
    private val mWriteKey: IKey = Key(mWriteLock)

    fun lockRead(): IKey {
        mReadLock.lock()
        return mReadKey
    }

    fun lockWrite(): IKey {
        mWriteLock.lock()
        return mWriteKey
    }

    fun <R> synchronizeRead(block: () -> R): R {
        val key = lockRead()
        try {
            return block()
        } finally {
            key.unlock()
        }
    }

    fun <R> synchronizeWrite(block: () -> R): R {
        val key = lockWrite()
        try {
            return block()
        } finally {
            key.unlock()
        }
    }
}