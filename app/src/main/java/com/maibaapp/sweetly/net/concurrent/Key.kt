package com.maibaapp.sweetly.net.concurrent

import java.util.concurrent.locks.Lock

class Key(private val mLock: Lock) : IKey {
    override fun unlock() {
        mLock.unlock()
    }

}