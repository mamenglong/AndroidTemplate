package com.mml.template.net.concurrent

class ConcurrentReference<T>(referent: T? = null) {
    private var mReferent: T? = referent
    private val mLock = ConciseReadWriteLock()

    fun set(referent: T?) = mLock.synchronizeWrite {
        mReferent = referent
    }

    fun get(): T? = mLock.synchronizeRead {
        mReferent
    }

    val isNull: Boolean
        get() = mLock.synchronizeRead {
            mReferent == null
        }

    val notNull: Boolean
        get() = mLock.synchronizeRead {
            mReferent != null
        }

    fun getAndSet(referent: T): T? = mLock.synchronizeWrite {
        val old = mReferent
        mReferent = referent
        old
    }

    fun setIfNull(referent: T): Boolean = mLock.synchronizeWrite {
        val result = mReferent == null
        if (result) {
            mReferent = referent
        }
        result
    }

    fun createIfNullAndGet(creator: () -> T): T = mLock.synchronizeWrite {
        val referent: T? = mReferent
        if (referent == null) {
            val result = creator()
            mReferent = result
            result
        } else {
            referent
        }
    }

    fun conditionalCreateAndGet(conditionAndCreator: (current: T?) -> T?): T? =
        mLock.synchronizeWrite {
            mReferent = conditionAndCreator(mReferent)
            mReferent
        }

    fun conditionalSet(referent: T, condition: (current: T?) -> Boolean): Boolean =
        mLock.synchronizeWrite {
            val result = condition(mReferent)
            if (result) {
                mReferent = referent
            }
            result
        }

}