package com.maibaapp.sweetly.util


import android.content.res.AssetFileDescriptor
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.ParcelFileDescriptor
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import okhttp3.ResponseBody
import java.io.*
import java.net.DatagramSocket
import java.nio.channels.FileLock
import java.util.concurrent.locks.Lock


object QuietFinalUtils {

    @JvmStatic
    fun close(vararg closeables: Any?) {
        for (closeable in closeables) {
            if (closeable == null) {
                continue
            }
            if (handleClose(closeable)) {
                continue
            }
            invokeFinal(closeable, "close")
        }
    }

    @JvmStatic
    fun recycle(vararg recyclables: Any?) {

        for (recyclable in recyclables) {
            if (recyclable == null) {
                continue
            }
            if (handleRecycle(recyclable)) {
                continue
            }
            invokeFinal(recyclable, "recycle")
        }
    }

    @JvmStatic
    fun flush(vararg flushables: Any?) {
        for (flushable in flushables) {
            if (flushable == null) {
                continue
            }
            if (handleFlush(flushable)) {
                continue
            }
            invokeFinal(flushable, "flush")
        }
    }

    @JvmStatic
    fun unlock(vararg locks: Any?) {
        for (lock in locks) {
            if (lock == null) {
                return
            }
            if (handleUnlock(lock)) {
                return
            }
            invokeFinal(lock, "unlock")
        }
    }

    @JvmStatic
    private fun handleUnlock(lock: Any): Boolean {
        var handle = false
        try {
            if (lock is FileLock) {
                handle = true
                lock.release()
            } else if (lock is Lock) {
                handle = true
                lock.unlock()
            }
        } catch (ignored: Exception) {
        }

        return handle
    }

    @JvmStatic
    private fun handleClose(closeable: Any): Boolean {
        var handle = false
        try {
            if (closeable is XmlResourceParser) {
                handle = true
                closeable.close()
            } else if (closeable is DatagramSocket) {
                handle = true
                closeable.close()
            } else if (closeable is ResponseBody) {
                handle = true
                closeable.close()
            } else if (closeable is FileOutputStream) {
                handle = true
                try {
                    closeable.fd.sync()
                } catch (ignored: Exception) {
                }
                closeable.close()
            } else if (closeable is OutputStream) {
                handle = true
                closeable.close()
            } else if (closeable is RandomAccessFile) {
                closeable.close()
            } else if (closeable is Cursor) {
                handle = true
                closeable.close()
            } else if (closeable is AssetFileDescriptor) {
                handle = true
                closeable.close()
            } else if (closeable is ParcelFileDescriptor) {
                handle = true
                closeable.close()
            } else if (closeable is InputStream) {
                handle = true
                closeable.close()
            } else if (closeable is Closeable) {
                handle = true
                closeable.close()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (closeable is AutoCloseable) {
                        handle = true
                        closeable.close()
                    }
                }
            }
        } catch (ignored: Exception) {
        }

        return handle
    }

    @JvmStatic
    private fun handleRecycle(recyclable: Any): Boolean {
        var handle = false
        try {
            if (recyclable is AccessibilityNodeInfoCompat) {
                handle = true
                recyclable.recycle()
            } else if (recyclable is AccessibilityNodeInfo) {
                handle = true
                recyclable.recycle()
            } else if (recyclable is AccessibilityEvent) {
                handle = true
                recyclable.recycle()
            } else if (recyclable is TypedArray) {
                handle = true
                recyclable.recycle()
            } else if (recyclable is Bitmap) {
                handle = true
                if (!recyclable.isRecycled) {
                    recyclable.recycle()
                }
            }
        } catch (ignored: Exception) {
        }

        return handle
    }

    @JvmStatic
    private fun handleFlush(flushable: Any): Boolean {
        var handle = false
        try {
            when (flushable) {
                is OutputStream -> {
                    handle = true
                    flushable.flush()
                }
                is Writer -> {
                    handle = true
                    flushable.flush()
                }
                is Flushable -> {
                    handle = true
                    flushable.flush()
                }
            }
        } catch (ignored: Exception) {
        }
        return handle
    }

    @JvmStatic
    private fun invokeFinal(`object`: Any?, name: String) {
        if (`object` == null) {
            return
        }

        val method = `object`.javaClass.getDeclaredMethodDeeplyAndSafely(name)
        if (method == null || !method.isAccessible) {
            return
        }

        try {
            method.invoke(`object`)
        } catch (ignored: Exception) {
        }

    }
}
