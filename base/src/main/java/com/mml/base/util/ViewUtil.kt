package com.mml.base.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.annotation.Dimension
import java.util.*

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/28 09:39
 * Description: This is ViewUtil
 * Package: com.withu.find.base.util
 * Project: With U
 */
object ViewUtil {
    /** Utils for view. */
        fun <T : View> findViewsWithType(root: View, type: Class<T>): List<T>? {
            val views: MutableList<T> = ArrayList()
            findViewsWithType(root, type, views)
            return views
        }

        private fun <T : View> findViewsWithType(
            view: View,
            type: Class<T>,
            views: MutableList<T>
        ) {
            if (type.isInstance(view)) {
                views.add(type.cast(view))
            }
            if (view is ViewGroup) {
                val viewGroup = view
                for (i in 0 until viewGroup.childCount) {
                    findViewsWithType(viewGroup.getChildAt(i), type, views)
                }
            }
        }

    /**
     * View 转 bitmap
     */
    fun View.view2Bitmap(): Bitmap {
        var ret = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(ret)
        var bgDrawable = this.background
        bgDrawable?.draw(canvas) ?: canvas.drawColor(Color.WHITE)
        this.draw(canvas)
        return ret
    }

    /**
     * @param visible 显示
     * @param gone 是否使用gone模式隐藏
     */
    fun View.extSetVisibility(visible: Boolean,gone:Boolean = false) = if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = if (gone) View.GONE else View.INVISIBLE
    }
    //扩展函数，view消失
    fun View.gone() {
        visibility = View.GONE
    }

    //扩展函数，view显示
    fun View.visible() {
        visibility = View.VISIBLE
    }
    //扩展函数，view隐藏
    fun View.invisible() {
        visibility = View.INVISIBLE
    }

//-----扩展属性-----

    // 扩展点击事件属性(重复点击时长)
    var <T : View> T.lastClickTime: Long
        set(value) = setTag(1766613352, value)
        get() = getTag(1766613352) as? Long ?: 0
    // 重复点击事件绑定
    inline fun <T : View> T.singleClick(time: Long = 800, crossinline block: (T) -> Unit) {
        setOnClickListener {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastClickTime > time || this is Checkable) {
                lastClickTime = currentTimeMillis
                block(this)
            }
        }
    }

    /**
     * 多次点击事件
     */
    fun View.multiClickListener(times:Int,action:()->Unit){
        val mHints = LongArray(times) //初始全部为0
        setOnClickListener {
            //将mHints数组内的所有元素左移一个位置
            System.arraycopy(mHints, 1, mHints, 0, mHints.size - 1)
            //获得当前系统已经启动的时间
            mHints[mHints.size - 1] = SystemClock.uptimeMillis()
            if (mHints[0] >= (mHints[mHints.size - 1] - 800)) {
                action()
            }
        }
    }



    var View.bottomMargin: Int
        get():Int {
            return (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        }
        set(value) {
            (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = value
        }


    var View.topMargin: Int
        get():Int {
            return (layoutParams as ViewGroup.MarginLayoutParams).topMargin
        }
        set(value) {
            (layoutParams as ViewGroup.MarginLayoutParams).topMargin = value
        }


    var View.rightMargin: Int
        get():Int {
            return (layoutParams as ViewGroup.MarginLayoutParams).rightMargin
        }
        set(value) {
            (layoutParams as ViewGroup.MarginLayoutParams).rightMargin = value
        }

    var View.leftMargin: Int
        get():Int {
            return (layoutParams as ViewGroup.MarginLayoutParams).leftMargin
        }
        set(value) {
            (layoutParams as ViewGroup.MarginLayoutParams).leftMargin = value
        }

    fun View.dpToPx(
        @Dimension(unit = Dimension.DP) dp: Float
    ): Float {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        )
    }
    /**
     * 扩展属性 dp转px
     */
    val Float.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    /**
     * 扩展属性 像素
     */
    val Float.px
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            this,
            Resources.getSystem().displayMetrics
        )
    /**
     * 扩展属性 sp转px
     */
    val Float.sp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this,
            Resources.getSystem().displayMetrics
        )
}