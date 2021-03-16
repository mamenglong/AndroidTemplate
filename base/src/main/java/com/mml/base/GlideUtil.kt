package com.mml.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-12-19 下午4:49
 * Description: This is GlideUtil
 * Package: com.m.l.tran.avatar.util
 * Project: TranAvatar
 */
object GlideUtil {
    fun loadDefault(url: Any?, view: ImageView) {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.ic_picture_preview)
            .error(R.drawable.ic_pic_fail)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view)

    }
    fun getFile(mContext: Context, url: String): File {
        return Glide.with(mContext)
            .download(url)
            .submit()
            .get()
    }
    fun loadWithCallBack(
        url: String,
        view: ImageView,
        onLoadFailed: (msg: String?) -> Unit = {},
        onResourceReady: (Drawable) -> Unit = {}
    ){
        val circularProgressDrawable = CircularProgressDrawable(view.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            setColorSchemeColors(Color.BLUE, Color.GRAY, Color.YELLOW)
            start()
        }
        Glide.with(view.context)
            .load(url)
            .transition(withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.let {
                        onLoadFailed.invoke(it.message)
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resource?.let {
                        onResourceReady.invoke(it)
                        return false
                    }
                    onLoadFailed.invoke("drawable is null")
                    return false
                }
            })
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_pic_fail)
            .into(view)
    }

    fun loadWithProgress(url: String, imageView: ImageView){
        val circularProgressDrawable = CircularProgressDrawable(imageView.context).apply { 
        strokeWidth = 5f
        centerRadius = 30f
        setColorSchemeColors(Color.BLUE, Color.GRAY, Color.YELLOW)
        start()
    }

        val requestOption : RequestOptions = RequestOptions()
            .placeholder(circularProgressDrawable)
            .error(R.drawable.ic_pic_fail)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()
        Glide.with(imageView.context)
            .load(url)
            .transition(withCrossFade())
            .apply(requestOption)
            .into(imageView)
    }

    fun loadBigPc(url: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .load(url)
            .into(object : CustomViewTarget<ImageView, Drawable>(imageView) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    imageView.setImageDrawable(resource)
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    super.onResourceLoading(placeholder)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {

                }
            }
            )
    }

    fun loadBitmapWithCallBack(
        url: Any,
        context: Context,
        isRound:Boolean = false,
        onLoadFailed: (msg: String?) -> Unit = {},
        onResourceReady: (Bitmap) -> Unit = {}
    ){
        Glide.with(context)
            .asBitmap()
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onResourceReady.invoke(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onLoadFailed.invoke("fail")
                }
            })
    }
    fun loadBitmapWithCallBack(
        url: Any,
        context: Context,
        listener: CustomTarget<Bitmap>
    ){
        Glide.with(context)
            .asBitmap()
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .load(url)
            .into(listener)
    }
    fun getGlide(context: Context): Glide {
        return Glide.get(context)
    }
}